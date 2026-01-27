package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDiscountRequest {

    @Schema(description = "Discount code", example = "SUMMER2025")
    @NotBlank(message = "Discount code cannot be blank")
    private String code;

    @Schema(description = "Discount percentage", example = "10.0")
    @Min(value = 0, message = "Discount percentage cannot be less than 0")
    @Max(value = 100, message = "Discount percentage cannot be greater than 100")
    private Double percentage;

    @Schema(description = "Is discount active?", example = "true")
    private Boolean active;
}
