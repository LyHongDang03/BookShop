package lyhongdang.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.BookRequest;
import lyhongdang.book.dto.request.UpdateBookRequest;
import lyhongdang.book.dto.request.response.BookPageResponse;
import lyhongdang.book.dto.request.response.BookResponse;
import lyhongdang.book.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book API")
public class BookController {

    private final BookService bookService;

    /* =======================================================
     *                CREATE
     * ======================================================= */
    @PostMapping
    @Operation(summary = "Create book", description = "Create a new book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<BookResponse> createBook(@RequestBody @Valid BookRequest bookRequest) {
        return ResponseEntity.status(201).body(bookService.createBook(bookRequest));
    }

    /* =======================================================
     *                READ
     * ======================================================= */
    @GetMapping
    @Operation(summary = "Get all books", description = "Get paginated list of all books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of books",
                    content = @Content(schema = @Schema(implementation = BookPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
            @Parameter(description = "Page number (default = 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (default = 10)") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get book details by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book details",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "Book id") @PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /* =======================================================
     *                UPDATE
     * ======================================================= */
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Update book information by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable int id,
            @RequestBody @Valid UpdateBookRequest updateBookRequest) {
        return ResponseEntity.ok(bookService.updateBook(id, updateBookRequest));
    }

    /* =======================================================
     *                DELETE
     * ======================================================= */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Delete a book by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Void> deleteBook(@Parameter(description = "Book id") @PathVariable Integer id) throws IOException {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /* =======================================================
     *                SEARCH
     * ======================================================= */
    @GetMapping("/by-categories")
    @Operation(summary = "Get books by categories", description = "Get paginated list of books by category ids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of books by categories",
                    content = @Content(schema = @Schema(implementation = BookPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "CategoryIds must not be null or empty", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<PageResponse<BookResponse>> getBooksByCategories(
            @Parameter(description = "List of category ids") @Schema(example = "[1,2,3]") @RequestBody List<Integer> categoryIds,
            @Parameter(description = "Page number (default = 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (default = 10)") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getBooksByCategories(categoryIds, page, size));
    }
}
