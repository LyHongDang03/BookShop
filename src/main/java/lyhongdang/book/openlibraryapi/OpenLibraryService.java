package lyhongdang.book.openlibraryapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lyhongdang.book.dto.request.response.BookResponse;
import lyhongdang.book.dto.request.response.CategoryResponse;
import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.Category;
import lyhongdang.book.entity.ImageCover;
import lyhongdang.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenLibraryService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://openlibrary.org";
    private static final String BASE_URL_GG = "https://www.googleapis.com/books/v1/volumes";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookResponse importBook(ImportBookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("Book with ISBN " + request.getIsbn() + " already exists.");
        }
        OpenLibraryBookDTO dto = fetchBookByIsbn(request.getIsbn());
        Book saved = bookRepository.save(mapFromDto(dto, request));
        return mapToBookResponse(saved);
    }


    public List<SearchBookResponse> searchBooksByTitle(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = BASE_URL_GG + "?q=intitle:" + encodedTitle;

            String json = restTemplate.getForObject(url, String.class);
            if (json == null || json.isEmpty()) return List.of();

            JsonNode root = objectMapper.readTree(json);
            JsonNode items = root.path("items");
            if (!items.isArray()) return List.of();

            List<SearchBookResponse> results = new ArrayList<>();
            for (JsonNode item : items) {
                JsonNode volumeInfo = item.path("volumeInfo");
                if (volumeInfo.isMissingNode()) continue;

                SearchBookResponse res = new SearchBookResponse();
                res.setTitle(volumeInfo.path("title").asText());

                // authors
                List<String> authors = new ArrayList<>();
                if (volumeInfo.has("authors")) {
                    volumeInfo.get("authors").forEach(a -> authors.add(a.asText()));
                }
                res.setAuthors(authors);

                // ISBNs
                List<String> isbns = new ArrayList<>();
                JsonNode ids = volumeInfo.path("industryIdentifiers");
                if (ids.isArray()) {
                    for (JsonNode id : ids) {
                        String type = id.path("type").asText();
                        String val = id.path("identifier").asText();
                        if (type.startsWith("ISBN")) isbns.add(val);
                    }
                }
                res.setIsbns(isbns);

                if (!isbns.isEmpty()) results.add(res);
            }

            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }




    private OpenLibraryBookDTO fetchBookByIsbn(String isbn) {
        String url = BASE_URL + "/isbn/" + isbn + ".json";
        return restTemplate.getForObject(url, OpenLibraryBookDTO.class);
    }

    private String fetchAuthorName(String authorKey) {
        try {
            String url = BASE_URL + authorKey + ".json";
            OpenLibraryAuthorDTO dto = restTemplate.getForObject(url, OpenLibraryAuthorDTO.class);
            return dto != null ? dto.getName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String fetchDescription(OpenLibraryBookDTO dto) {
        try {
            if (dto.getWorks() != null && !dto.getWorks().isEmpty()) {
                String workKey = dto.getWorks().getFirst().get("key");
                String url = BASE_URL + workKey + ".json";
                Map<?, ?> workData = restTemplate.getForObject(url, Map.class);

                if (workData != null && workData.get("description") != null) {
                    Object desc = workData.get("description");
                    if (desc instanceof String) return (String) desc;
                    if (desc instanceof Map) return (String) ((Map<?, ?>) desc).get("value");
                }
            }
        } catch (Exception e) {
            // ignore lỗi
        }
        return "Imported from OpenLibrary";
    }

    private Book mapFromDto(OpenLibraryBookDTO dto, ImportBookRequest request) {
        String author = "Unknown";
        if (dto.getAuthors() != null && !dto.getAuthors().isEmpty()) {
            String authorKey = dto.getAuthors().getFirst().get("key");
            author = fetchAuthorName(authorKey);
        }

        String description = fetchDescription(dto);

        Book book = Book.builder()
                .nameBook(dto.getTitle())
                .author(author)
                .isbn(request.getIsbn())
                .publisher(dto.getPublishers() != null && !dto.getPublishers().isEmpty()
                        ? dto.getPublishers().getFirst()
                        : "Unknown")
                .year(extractYear(dto.getPublish_date()))
                .pages(dto.getNumber_of_pages() != null ? dto.getNumber_of_pages() : 0)
                .description(description)
                .owner(request.getOwner())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        if (dto.getSubjects() != null && !dto.getSubjects().isEmpty()) {
            List<Category> categories = dto.getSubjects().stream()
                    .map(this::normalizeSubject)
                    .map(sub -> categoryRepository.findByNameCategory(sub)
                            .orElseGet(() -> {
                                Category newCat = new Category();
                                newCat.setNameCategory(sub);
                                return categoryRepository.save(newCat);
                            }))
                    .toList();
            book.setCategories(categories);
        }

        ImageCover cover = new ImageCover();
        cover.setImageUrl("https://covers.openlibrary.org/b/isbn/" + request.getIsbn() + "-L.jpg");
        cover.setBook(book);
        book.setImageCover(cover);

        return book;
    }

    private String normalizeSubject(String subject) {
        if (subject == null) return "Unknown";
        // Viết hoa chữ cái đầu, bỏ ký tự lạ
        String normalized = subject.trim()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s{2,}", " ");
        return normalized.substring(0, 1).toUpperCase() + normalized.substring(1);
    }

    private int extractYear(String publishDate) {
        if (publishDate == null) return 0;
        String year = publishDate.replaceAll("\\D+", "");
        return year.isEmpty() ? 0 : Integer.parseInt(year);
    }

    private BookResponse mapToBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getNameBook())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .year(book.getYear())
                .pages(book.getPages())
                .price(book.getPrice())
                .quantity(book.getQuantity())
                .owner(book.getOwner())
                .coverImageUrl(book.getImageCover() != null ? book.getImageCover().getImageUrl() : null)
                .category(book.getCategories() != null
                        ? book.getCategories().stream()
                        .map(c -> new CategoryResponse(c.getNameCategory()))
                        .toList()
                        : null)
                .build();
    }
}
