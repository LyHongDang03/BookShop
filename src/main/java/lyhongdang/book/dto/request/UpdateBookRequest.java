package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookRequest {

    @Schema(description = "Name of the book", example = "The Great Gatsby")
    private String nameBook;

    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;

    @Schema(description = "ISBN of the book", example = "978-0-7432-7356-5")
    private String isbn;

    @Schema(description = "Description of the book", example = "A story about the American Dream...")
    private String description;

    @Schema(description = "Publisher of the book", example = "Scribner")
    private String publisher;

    @Schema(description = "Publication year", example = "1925")
    private int year;

    @Schema(description = "Number of pages", example = "180")
    private int pages;

    @Schema(description = "Owner of the book", example = "John Doe")
    private String owner;

    @Schema(description = "Price of the book", example = "29.99")
    private Double price;

    @Schema(
            description = "List of category IDs for the book",
            example = "[1, 2, 3]"
    )
    private List<Integer> categories;
}
