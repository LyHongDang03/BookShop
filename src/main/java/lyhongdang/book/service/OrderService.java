package lyhongdang.book.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.CheckoutRequest;
import lyhongdang.book.dto.request.response.OrderDetailResponse;
import lyhongdang.book.dto.request.response.OrderResponse;
import lyhongdang.book.dto.request.response.OrderStatsResponse;
import lyhongdang.book.dto.request.response.ViewOrderResponse;
import lyhongdang.book.entity.*;
import lyhongdang.book.enums.EmailTemplateName;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.enums.OrderStatus;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final EmailService emailService;
    private final DiscountRepository discountRepository;

    /**
     * Perform offline checkout:
     * 1. Validate cart.
     * 2. Create order and orderDetails.
     * 3. Reduce inventory.
     * 4. Delete purchased cart items.
     * 5. Send order confirmation email.
     * 6. Confirm order successfully.
     */
    @Transactional
    public void checkoutOffline(CheckoutRequest request) {
        User user = getCurrentUser();
        Cart cart = validateCart(user);

        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCodes.CART_ITEM_NOT_SELECTED);
        }
        Order order = createOrder(user, cartItems, request.getDiscountCode());
        cartItemRepository.deleteAll(cartItems);
        cart.getItems().removeAll(cartItems);
        cartRepository.save(cart);
        sendOrderConfirmation(user, order);
    }

    /**
     * Create a pending order for online payment.
     * Does not reduce stock or delete cart items.
     */
    @Transactional
    public Order createPendingOrder(CheckoutRequest request) {
        User user = getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCodes.CART_ITEM_NOT_SELECTED);
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        double totalPrice = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();
            if (cartItem.getQuantity() > book.getQuantity()) {
                throw new BusinessException(ErrorCodes.BOOK_OUT_OF_STOCK);
            }
            double itemTotal = book.getPrice() * cartItem.getQuantity();
            totalPrice += itemTotal;

            OrderDetail detail = new OrderDetail();
            detail.setBook(book);
            detail.setOrder(order);
            detail.setQuantity(cartItem.getQuantity());
            detail.setTotalPrice(itemTotal);
            orderDetails.add(detail);
        }
        order.setTotalPrice(totalPrice);
        order.setOrderDetails(orderDetails);
        return orderRepository.save(order);
    }

    /**
     * Get Order entity by ID (for online payment verification).
     * Checks user's authorization.
     */
    @Transactional
    public Order getOrderEntityById(int orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORDER_NOT_FOUND));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCodes.ORDER_UNAUTHORIZED);
        }
        return order;
    }

    /**
     * Validate cart existence and items.
     */
    private Cart validateCart(User user) {
        Cart cart = user.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new BusinessException(ErrorCodes.CART_EMPTY);
        }
        return cart;
    }

    /**
     * Create an order with CartItems and apply discount if available.
     * Reduce inventory and save order in DB.
     */
    private Order createOrder(User user, List<CartItem> cartItems, String discountCode) {
        double totalPrice = 0;
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();
            if (cartItem.getQuantity() > book.getQuantity()) {
                throw new BusinessException(ErrorCodes.BOOK_OUT_OF_STOCK);
            }
            double itemTotal = book.getPrice() * cartItem.getQuantity();
            totalPrice += itemTotal;

            OrderDetail detail = new OrderDetail();
            detail.setBook(book);
            detail.setOrder(order);
            detail.setQuantity(cartItem.getQuantity());
            detail.setTotalPrice(itemTotal);
            orderDetails.add(detail);

            book.setQuantity(book.getQuantity() - cartItem.getQuantity());
        }

        totalPrice = applyDiscount(discountCode, totalPrice);
        order.setTotalPrice(totalPrice);
        order.setOrderDetails(orderDetails);
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    /**
     * Apply discount code if present.
     */
    private double applyDiscount(String discountCode, double totalPrice) {
        if (discountCode == null || discountCode.isEmpty()) {
            return totalPrice;
        }
        Discount discount = discountRepository.findByCodeAndActiveTrue(discountCode)
                .orElseThrow(() -> new BusinessException(ErrorCodes.DISCOUNT_NOT_FOUND));
        double discountAmount = totalPrice * (discount.getPercentage() / 100.0);
        return totalPrice - discountAmount;
    }

    /**
     * Send order confirmation email.
     */
    private void sendOrderConfirmation(User user, Order order) {
        try {
            emailService.sendOrderConfirmationEmail(
                    user.getEmail(),
                    user.fullName(),
                    EmailTemplateName.ORDER_CONFIRMATION,
                    order,
                    "Your Book Order Confirmation"
            );
        } catch (MessagingException e) {
            log.warn("Failed to send order confirmation email for order ID: {}", order.getId(), e);
        }
    }

    /**
     * Get order by ID and return DTO.
     * Checks user's authorization.
     */
    @Transactional
    public OrderResponse getOrderById(int orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.ORDER_NOT_FOUND));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCodes.ORDER_UNAUTHORIZED);
        }
        List<OrderDetailResponse> details = order.getOrderDetails().stream()
                .map(this::toOrderDetailResponse)
                .collect(Collectors.toList());

        return new OrderResponse(order.getId(), order.getOrderDate(), order.getTotalPrice(), details);
    }

    /**
     * Get paginated order list for current user.
     */
    public PageResponse<ViewOrderResponse> getOrdersByPage(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        Page<Order> orderPage = orderRepository.findByUser(user, pageable);

        List<ViewOrderResponse> content = orderPage.getContent().stream()
                .map(order -> ViewOrderResponse.builder()
                        .orderId(order.getId())
                        .orderDate(order.getOrderDate())
                        .totalPrice(order.getTotalPrice())
                        .build())
                .toList();

        return PageResponse.<ViewOrderResponse>builder()
                .content(content)
                .number(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .build();
    }

    /**
     * Convert OrderDetail to DTO.
     */
    private OrderDetailResponse toOrderDetailResponse(OrderDetail detail) {
        return OrderDetailResponse.builder()
                .nameBook(detail.getBook().getNameBook())
                .quantity(detail.getQuantity())
                .totalPrice(detail.getTotalPrice())
                .build();
    }

    /**
     * Get order statistics by specific date.
     */
    public OrderStatsResponse getStatsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);

        long totalOrders = orders.size();
        double totalAmount = orders.stream().mapToDouble(Order::getTotalPrice).sum();

        return new OrderStatsResponse(totalOrders, totalAmount);
    }

    /**
     * Get today's order statistics.
     */
    public OrderStatsResponse getTodayStats() {
        return getStatsByDate(LocalDate.now());
    }

    /**
     * Send today's order report via email.
     */
    public void sendTodayReport(String email) {
        LocalDate today = LocalDate.now();
        OrderStatsResponse stats = getStatsByDate(today);

        try {
            emailService.sendDailyReport(email, today, stats.getTotalOrders(), stats.getTotalAmount());
        } catch (MessagingException e) {
            log.error("Failed to send daily report email to {}", email, e);
            throw new BusinessException(ErrorCodes.EMAIL_SENDING_FAILED);
        }
    }

    /**
     * Get user.
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND));
    }
}
