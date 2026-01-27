package lyhongdang.book.dto.request.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    @Schema(description = "Id of order", example = "1")
    private int orderId;
    @Schema(description = "Date of order", example = "2021-03-01T10:15:30")
    private LocalDateTime orderDate;
    @Schema(description = "Total price of order", example = "29.99")
    private double totalPrice;
    @Schema(description = "List of order details")
    private List<OrderDetailResponse> orderDetails;
}
