package lyhongdang.book.dto.request.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {

    @Schema(description = "ID of the book", example = "1")
    private Integer bookId;

    @Schema(description = "Name of the book", example = "Spring Boot in Action")
    private String nameBook;

    @Schema(description = "Quantity purchased", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price of the book", example = "10.0")
    private Double price;

    @Schema(description = "Total price for this item", example = "20.0")
    private Double totalPrice;

    @Schema(description = "URL of the book cover image", example = "https://example.com/book-cover.jpg")
    private String coverImageUrl;
}
