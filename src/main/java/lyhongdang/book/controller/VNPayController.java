package lyhongdang.book.controller;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.CheckoutRequest;
import lyhongdang.book.service.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VNPayController {
    private final VNPayService vnPayService;
    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutOnline(@RequestBody CheckoutRequest checkoutRequest) {
        try {
            String paymentUrl = vnPayService.checkoutOnline(checkoutRequest);
            return ResponseEntity.ok(paymentUrl);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(500).body("Lỗi tạo URL thanh toán VNPay");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/return")
    public ResponseEntity<String> paymentReturn(@RequestParam("vnp_ResponseCode") String responseCode,
                                                @RequestParam("vnp_TxnRef") int orderId) {
        return vnPayService.handlePaymentReturn(responseCode, orderId);
    }
}
