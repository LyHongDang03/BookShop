package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    @NotBlank(message = "Book name cannot be empty")
    @Schema(description = "Name of book", example = "The Great Gatsby")
    private String nameBook;
    @NotBlank(message = "Author cannot be empty")
    @Schema(description = "Author of book", example = "F. Scott Fitzgerald")
    private String author;
    @Pattern(
            regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
            message = "Invalid ISBN format. Must be ISBN-10 or ISBN-13."
    )
    @Schema(description = "ISBN of book", example = "978-0-7432-7356-5")
    private String isbn;
    @Schema(description = "Description of book", example = "A story about the American Dream...")
    private String description;
    @Schema(description = "Publisher of book", example = "Scribner")
    private String publisher;
    @Min(value = 1000, message = "Year must be valid")
    @Schema(description = "Year of book", example = "1925")
    private int year;
    @Min(value = 1, message = "Pages must be greater than 0")
    @Schema(description = "Pages of book", example = "180")
    private int pages;
    @Schema(description = "Owner of book", example = "John Doe")
    private String owner;
    @Schema(description = "Price of book", example = "29.99")
    @Positive(message = "Price must be greater than 0")
    private double price;
    @Min(value = 0, message = "Quantity cannot be negative")
    @Schema(description = "Quantity of book", example = "10")
    private int quantity;
    @Schema(description = "List of category IDs", example = "[1, 2, 3]")
    private List<Integer> categories;
}
