package com.example.book.book;

import com.example.book.category.Category;
import com.example.book.category.CategoryRepository;
import com.example.book.image.imageCover.ImageCoverService;
import com.example.book.image.imagesDescription.ImagesDescription;
import com.example.book.image.imagesDescription.ImagesDescriptionSerVice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ImageCoverService imageCoverService;
    private final ImagesDescriptionSerVice imagesDescriptionSerVice;
    public Book createBook(BookRequest bookRequest) {
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
            List<Category> category = categoryRepository.findAllById(bookRequest.getCategories());
            book.setCategories(category);
        }
        return bookRepository.save(book);
    }

    public void deleteBook(int bookId) throws IOException {
        var book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        for (Category category : book.getCategories()) {
            category.getBooks().remove(book);
        }
        var imageCoverId = book.getImageCover().getId();
//        book.setImageCover(null);
        imageCoverService.deleteCover(imageCoverId);
        for (ImagesDescription imagesDescription: book.getImages()){
            imagesDescriptionSerVice.deleteImage(imagesDescription.getId());
        }
        bookRepository.delete(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(int bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public Book updateBook(int bookId, UpdateBookRequest updateBookRequest) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setNameBook(updateBookRequest.getNameBook());
        book.setAuthor(updateBookRequest.getAuthor());
        book.setIsbn(updateBookRequest.getIsbn());
        book.setDescription(updateBookRequest.getDescription());
        book.setPublisher(updateBookRequest.getPublisher());
        book.setYear(updateBookRequest.getYear());
        book.setPages(updateBookRequest.getPages());
        book.setOwner(updateBookRequest.getOwner());
        if (updateBookRequest.getCategories() != null) {
            List<Category> category = categoryRepository.findAllById(updateBookRequest.getCategories());
            book.setCategories(category);
        }
        return bookRepository.save(book);
    }
}
