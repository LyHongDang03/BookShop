package com.example.book.oder;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderSerVice orderService;
    @PostMapping("/checkout")
    public ResponseEntity<String> checkOut(@RequestBody List<Integer> bookIds) {
        orderService.checkOut(bookIds);
        return ResponseEntity.ok("Thanh toán thành công");
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAllOrders();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable int id) {
        var rs = orderService.getOrderById(id);
        return ResponseEntity.ok(rs);
    }
}
