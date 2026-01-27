package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {

    @Schema(
            description = "List of cart item IDs selected for checkout",
            example = "[1, 2, 3]"
    )
    @NotEmpty(message = "Cart item IDs cannot be empty")
    private List<Integer> cartItemIds;

    @Schema(
            description = "Optional discount code applied to the order",
            example = "DISCOUNT10"
    )
    private String discountCode;
}
