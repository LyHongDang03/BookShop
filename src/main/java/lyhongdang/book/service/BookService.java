package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.BookRequest;
import lyhongdang.book.dto.request.UpdateBookRequest;
import lyhongdang.book.dto.request.response.BookResponse;
import lyhongdang.book.dto.request.response.CategoryResponse;
import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.Category;
import lyhongdang.book.entity.ImagesDescription;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.BookRepository;
import lyhongdang.book.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ImageCoverService imageCoverService;
    private final ImagesDescriptionSerVice imagesDescriptionSerVice;

    /* -------------------- PUBLIC METHODS -------------------- */

    @PreAuthorize("hasAnyRole('ADMIN')")
    @CachePut(value = "books", key = "#result.id")
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = Book.builder()
                .nameBook(bookRequest.getNameBook())
                .author(bookRequest.getAuthor())
                .isbn(bookRequest.getIsbn())
                .description(bookRequest.getDescription())
                .publisher(bookRequest.getPublisher())
                .year(bookRequest.getYear())
                .pages(bookRequest.getPages())
                .owner(bookRequest.getOwner())
                .price(bookRequest.getPrice())
                .quantity(bookRequest.getQuantity())
                .build();

        if (bookRequest.getCategories() != null && !bookRequest.getCategories().isEmpty()) {
            book.setCategories(categoryRepository.findAllById(bookRequest.getCategories()));
        }

        return mapToBookResponse(bookRepository.save(book));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @CacheEvict(value = "books", key = "#bookId")
    public void deleteBook(int bookId) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));

        book.getCategories().forEach(c -> c.getBooks().remove(book));

        if (book.getImageCover() != null) {
            imageCoverService.deleteCover(book.getImageCover().getId());
        }

        for (ImagesDescription img : book.getImages()) {
            imagesDescriptionSerVice.deleteImage(img.getId());
        }

        bookRepository.delete(book);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Cacheable(value = "bookPages", key = "{#page, #size}")
    public PageResponse<BookResponse> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        Page<Book> bookPage = bookRepository.findAll(pageable);
        List<BookResponse> responses = bookPage.getContent()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());

        return buildPageResponse(bookPage, responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public PageResponse<BookResponse> getBooksByCategories(List<Integer> categoryIds, int page, int size) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BusinessException(ErrorCodes.CATEGORY_NOT_ALLOWED);
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        Page<Book> bookPage = bookRepository.findDistinctByCategories_IdIn(categoryIds, pageable);

        List<BookResponse> responses = bookPage.getContent()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());

        return buildPageResponse(bookPage, responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Cacheable(value = "books", key = "#bookId")
    public BookResponse getBookById(int bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));
        return mapToViewBookResponse(book);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @CachePut(value = "books", key = "#bookId")
    public BookResponse updateBook(int bookId, UpdateBookRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));

        book.setNameBook(request.getNameBook());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPublisher(request.getPublisher());
        book.setYear(request.getYear());
        book.setPages(request.getPages());
        book.setOwner(request.getOwner());

        if (request.getCategories() != null) {
            book.setCategories(categoryRepository.findAllById(request.getCategories()));
        }

        return mapToBookResponse(bookRepository.save(book));
    }

    /* -------------------- PRIVATE HELPERS -------------------- */

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
                .category(book.getCategories() != null ? mapToCategoryResponses(book.getCategories()) : null)
                .build();
    }

    private List<CategoryResponse> mapToCategoryResponses(List<Category> categories) {
        return categories.stream()
                .map(c -> new CategoryResponse(c.getNameCategory()))
                .collect(Collectors.toList());
    }

    private BookResponse mapToViewBookResponse(Book book) {
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
                .images(book.getImages().stream().map(ImagesDescription::getImageUrl).collect(Collectors.toList()))
                .build();
    }

    private <T> PageResponse<T> buildPageResponse(Page<?> page, List<T> content) {
        return PageResponse.<T>builder()
                .content(content)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}