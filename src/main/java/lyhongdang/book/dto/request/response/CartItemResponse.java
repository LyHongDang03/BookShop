package lyhongdang.book.dto.request.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    @Schema(description = "Book id", example = "1")
    private int id;
    @Schema(description = "Name of the book", example = "The Great Gatsby")
    private String name;
    @Schema(description = "Author of the book", example = "HBN")
    private String author;
    @Schema(description = "ISBN of the book", example = "978-0-7432-7356-5")
    private String isbn;
    @Schema(description = "Quantity of the book", example = "1")
    private int quantity;
    @Schema(description = "URL of the book cover image", example = "https://example.com/book-cover.jpg")
    private String coverImage;
    @Schema(description = "Price of the book", example = "29.99")
    private double price;
    @Schema(description = "Total price of the book", example = "29.99")
    private double total;
}
