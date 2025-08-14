package com.example.book.oder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderDetail")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderSerVice orderSerVice;
    @GetMapping("{id}")
    public OrderDetail orderDetail(@PathVariable int id) {
        return orderSerVice.getOrderDetailById(id);
    }
}
