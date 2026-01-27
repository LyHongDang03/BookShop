package lyhongdang.book.dto.request.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewOrderResponse {

    @Schema(description = "Unique identifier of the order", example = "101")
    private int orderId;

    @Schema(description = "Date and time when the order was placed", example = "2025-09-25T14:30:00")
    private LocalDateTime orderDate;

    @Schema(description = "Total price of the order", example = "199.99")
    private double totalPrice;
}
