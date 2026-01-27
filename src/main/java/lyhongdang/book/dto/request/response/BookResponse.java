package lyhongdang.book.dto.request.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {
    @Schema(description = "Book ID", example = "1")
    private Integer id;
    @Schema(description = "Name of the book", example = "The Great Gatsby")
    private String name;
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;
    @Schema(description = "ISBN of the book", example = "978-0-7432-7356-5")
    private String isbn;
    @Schema(description = "Description of the book", example = "A story about the American Dream...")
    private String description;
    @Schema(description = "Publisher of the book", example = "Scribner")
    private String publisher;
    @Schema(description = "Publication year", example = "1925")
    private Integer year;
    @Schema(description = "Number of pages", example = "180")
    private Integer pages;
    @Schema(description = "Price of the book", example = "29.99")
    private Double price;
    @Schema(description = "Owner of the book", example = "John Doe")
    private String owner;
    @Schema(description = "Available quantity", example = "5")
    private Integer quantity;
    @Schema(
            description = "Categories of the book",
            example = "[{\"name\": \"A\", \"description\": \"Fiction\"}, {\"name\": \"B\", \"description\": \"Science\"}, {\"name\": \"C\", \"description\": \"History\"}]"
    )
    private List<CategoryResponse> category;
    @Schema(description = "URL of the book cover image", example = "https://example.com/book-cover.jpg")
    private String coverImageUrl;
    @Schema(description = "List of image URLs associated with the book")
    private List<String> images;
}
