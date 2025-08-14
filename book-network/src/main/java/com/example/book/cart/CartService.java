package com.example.book.cart;

import com.example.book.book.Book;
import com.example.book.book.BookRepository;
import com.example.book.cartItem.CartItem;
import com.example.book.user.User;
import com.example.book.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public void addToCart(Integer bookId, Integer quantity) {

        User user = getCurrentUser();
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            user.setCart(cart);
            cart.setUser(user);
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.getQuantity() < quantity) {
            throw new RuntimeException("Sản phẩm \"" + book.getNameBook() + "\" không đủ hàng trong kho.");
        }
        List<CartItem> items = cart.getItems();
        CartItem existing = null;
        for (CartItem item : items) {
            if (item.getBook().getId().equals(bookId)) {
                existing = item;
                break;
            }
        }
        if (existing == null) {
            CartItem item = new CartItem();
            item.setBook(book);
            item.setQuantity(quantity);
            item.setCart(cart);
            items.add(item);
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
        cartRepository.save(cart);
    }

    public Cart viewCart() {
        User user = getCurrentUser();
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            user.setCart(cart);
            cart.setUser(user);
            cartRepository.save(cart);
        }
        return cart;
    }

    public void updateCart(Integer bookId, Integer quantity) {

        User user = getCurrentUser();

        Cart cart = user.getCart();
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        List<CartItem> items = cart.getItems();
        CartItem existing = null;
        for (CartItem item : items) {
            if (item.getBook().getId().equals(bookId)) {
                existing = item;
                break;
            }
        }
        if (existing == null) {
            throw new RuntimeException("Book not found in cart");
        }
        if (quantity <= 0) {
            items.remove(existing);
        } else {
            existing.setQuantity(quantity);
        }
        cartRepository.save(cart);
    }

    public void removeFromCart(Integer bookId, Integer quantity) {

        User user = getCurrentUser();

        Cart cart = user.getCart();
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        List<CartItem> items = cart.getItems();
        CartItem existing = null;
        for (CartItem item : items) {
            if (item.getBook().getId().equals(bookId)) {
                existing = item;
                break;
            }
        }
        if (existing == null) {
            throw new RuntimeException("Book not found in cart");
        } else {
            items.remove(existing);
        }
        cartRepository.save(cart);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
