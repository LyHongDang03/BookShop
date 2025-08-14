package com.example.book.oder;

import com.example.book.book.Book;
import com.example.book.cart.Cart;
import com.example.book.cart.CartItemRepository;
import com.example.book.cart.CartRepository;
import com.example.book.cartItem.CartItem;
import com.example.book.user.User;
import com.example.book.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSerVice {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional
    public void checkOut(List<Integer> cartItemIds) {
        User user = getCurrentUser();
        Cart cart = user.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("No items found in cart");
        }
        double totalPrice = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();
            if (cartItem.getQuantity() > book.getQuantity()) {
                throw new RuntimeException("Not enough stock");
            }
            double itemTotalPrice = book.getPrice() * cartItem.getQuantity();
            totalPrice += itemTotalPrice;
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setBook(book);
            orderDetail.setOrder(order);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setTotalPrice(itemTotalPrice);
            orderDetails.add(orderDetail);
            book.setQuantity(book.getQuantity() - cartItem.getQuantity());
        }

        order.setOrderDetails(orderDetails);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);
        cart.getItems().removeAll(cartItems);
        cartRepository.save(cart);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    List<Order> findAllOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUser(user);
    }

    public OrderResponse getOrderById(int orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            if (orderDetail.getOrder().getId().equals(orderId)) {
                OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                orderDetailResponse.setNameBook(orderDetail.getBook().getNameBook());
                orderDetailResponse.setQuantity(orderDetail.getQuantity());
                orderDetailResponse.setTotalPrice(orderDetail.getTotalPrice());
                orderDetailResponses.add(orderDetailResponse);
            }
        }

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setOrderDetails(orderDetailResponses);
        return orderResponse;
    }
    public OrderDetail getOrderDetailById(int orderDetailId) {
        User user = getCurrentUser();
        OrderDetail orderDetail = orderDetailRepository.findByOrderId(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Order detail not found"));
        if (!orderDetail.getOrder().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return orderDetail;
    }
}
