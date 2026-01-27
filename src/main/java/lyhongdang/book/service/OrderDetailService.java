package lyhongdang.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lyhongdang.book.dto.request.response.OrderDetailResponse;
import lyhongdang.book.entity.OrderDetail;
import lyhongdang.book.entity.User;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.OrderDetailRepository;
import lyhongdang.book.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderDetailResponse getOrderDetailById(int orderDetailId) {
        User user = getCurrentUser();

        OrderDetail orderDetail = orderDetailRepository.findByIdWithOrderAndUser(orderDetailId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORDER_DETAIL_NOT_FOUND));

        if (!orderDetail.getOrder().getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCodes.ORDER_UNAUTHORIZED);
        }

        return mapToResponse(orderDetail);
    }

    public OrderDetailResponse mapToResponse(OrderDetail detail) {
        return OrderDetailResponse.builder()
                .bookId(detail.getBook().getId())
                .nameBook(detail.getBook().getNameBook())
                .quantity(detail.getQuantity())
                .price(detail.getBook().getPrice())
                .totalPrice(detail.getTotalPrice())
                .coverImageUrl(detail.getBook().getImageCover().getImageUrl())
                .build();
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND));
    }
}
