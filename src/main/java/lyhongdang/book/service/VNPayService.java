package lyhongdang.book.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lyhongdang.book.config.VnPayConfig;
import lyhongdang.book.dto.request.CheckoutRequest;
import lyhongdang.book.entity.*;
import lyhongdang.book.enums.EmailTemplateName;
import lyhongdang.book.enums.OrderStatus;
import lyhongdang.book.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private final VnPayConfig vnPayConfig;
    private final OrderService orderService;
    private final EmailService emailService;
    private final OrderRepository orderRepository;

    /**
     * Create an online checkout order (PENDING) and return VNPay payment URL.
     */
    @Transactional
    public String checkoutOnline(CheckoutRequest checkoutRequest) throws UnsupportedEncodingException {

        // Create pending order
        Order order = orderService.createPendingOrder(checkoutRequest);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = (long) (order.getTotalPrice() * 100); // VNPay amount in VND * 100

        String bankCode = "NCB";
        String vnp_TxnRef = String.valueOf(order.getId());
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = vnPayConfig.getTmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Order payment: " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(calendar.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Expire 15 minutes later
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build query string and hash data
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append('&');
                hashData.append('&');
            }
        }

        if (!query.isEmpty()) query.setLength(query.length() - 1);
        if (!hashData.isEmpty()) hashData.setLength(hashData.length() - 1);

        // Generate secure hash
        String vnp_SecureHash = VnPayConfig.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnPayConfig.getVnpUrl() + "?" + query;
    }

    /**
     * Handle VNPay payment return callback.
     * Update order status and inventory based on payment result.
     */
    @Transactional
    public ResponseEntity<String> handlePaymentReturn(String responseCode, int orderId) {
        Order order = orderService.getOrderEntityById(orderId);

        if ("00".equals(responseCode)) { // Payment success
            if (order.getStatus() == OrderStatus.PAID) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Order has already been paid!");
            }

            order.setStatus(OrderStatus.PAID);

            // Reduce book quantity
            for (OrderDetail detail : order.getOrderDetails()) {
                Book book = detail.getBook();
                book.setQuantity(book.getQuantity() - detail.getQuantity());
            }

            // Remove purchased items from user's cart
            Cart cart = order.getUser().getCart();
            cart.getItems().removeIf(item ->
                    order.getOrderDetails().stream()
                            .anyMatch(d -> d.getBook().getId().equals(item.getBook().getId()))
            );

            sendOrderConfirmation(order.getUser(), order);
            return ResponseEntity.ok("Payment successful!");
        } else { // Payment failed
            order.setStatus(OrderStatus.CANCELLED);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Payment failed! Response code: " + responseCode);
        }
    }

    /**
     * Send order confirmation email to user.
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
}
