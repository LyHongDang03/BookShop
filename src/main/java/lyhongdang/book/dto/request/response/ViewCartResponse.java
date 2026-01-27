package lyhongdang.book.dto.request.response;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewCartResponse {
    List<CartItemResponse> cartItems;
}
