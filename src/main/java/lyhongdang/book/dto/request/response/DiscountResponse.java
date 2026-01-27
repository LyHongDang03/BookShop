package lyhongdang.book.dto.request.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountResponse {
    @Schema(description = "Discount ID", example = "1")
    private Integer id;
    @Schema(description = "Discount code", example = "DISCOUNT10")
    private String code;
    @Schema(description = "Percentage of discount", example = "10.0")
    private Double percentage;
    @Schema(description = "Active", example = "true")
    private boolean active;
}
