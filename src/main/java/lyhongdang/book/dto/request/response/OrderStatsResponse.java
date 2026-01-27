package lyhongdang.book.dto.request.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatsResponse {
    @Schema(description = "Total number of orders", example = "10")
    private long totalOrders;
    @Schema(description = "Total amount of orders", example = "100.00")
    private double totalAmount;
}
