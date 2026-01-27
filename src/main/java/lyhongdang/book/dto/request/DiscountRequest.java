package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountRequest {

    @Schema(
            description = "Unique discount code",
            example = "SUMMER2025"
    )
    @NotBlank(message = "Discount code cannot be blank")
    private String code;

    @Schema(
            description = "Discount percentage (0 - 100)",
            example = "15.5"
    )
    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Percentage must be greater than 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Percentage cannot be greater than 100")
    private Double percentage;
}
