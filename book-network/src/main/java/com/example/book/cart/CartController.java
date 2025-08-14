package com.example.book.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Integer bookId, @RequestParam Integer quantity) {
        cartService.addToCart(bookId, quantity);
        return "Added to cart successfully";
    }

    @GetMapping
    public Cart viewCart() {
        return cartService.viewCart();
    }

    @PutMapping("/update")
    public String updateCart(@RequestParam Integer bookId, @RequestParam Integer quantity) {
        cartService.updateCart(bookId, quantity);
        return "Cart updated successfully";
    }

    @DeleteMapping("/remove")
    public String removeFromCart(@RequestParam Integer bookId) {
        cartService.removeFromCart(bookId, 0);
        return "Removed from cart successfully";
    }
}
