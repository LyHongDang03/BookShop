package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.response.CartItemResponse;
import lyhongdang.book.dto.request.response.ViewCartResponse;
import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.Cart;
import lyhongdang.book.entity.CartItem;
import lyhongdang.book.entity.User;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.BookRepository;
import lyhongdang.book.repository.CartRepository;
import lyhongdang.book.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /* -------------------- PUBLIC METHODS -------------------- */

//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void addToCart(Integer bookId, Integer quantity) {
        Cart cart = getOrCreateCart();
        Book book = findBook(bookId);

        if (book.getQuantity() < quantity) {
            throw new BusinessException(ErrorCodes.BOOK_OUT_OF_STOCK);
        }

        CartItem item = findCartItem(cart, bookId);
        if (item == null) {
            item = new CartItem();
            item.setBook(book);
            item.setQuantity(quantity);
            item.setCart(cart);
            cart.getItems().add(item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        cartRepository.save(cart);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ViewCartResponse viewCart() {
        Cart cart = getOrCreateCart();

        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        return ViewCartResponse.builder()
                .cartItems(items)
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void updateCart(Integer bookId, Integer quantity) {
        Cart cart = getExistingCart();
        CartItem item = findCartItemOrThrow(cart, bookId);

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        cartRepository.save(cart);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void removeFromCart(Integer bookId) {
        Cart cart = getExistingCart();
        CartItem item = findCartItemOrThrow(cart, bookId);

        cart.getItems().remove(item);
        cartRepository.save(cart);
    }

    /* -------------------- PRIVATE HELPERS -------------------- */

    private Cart getOrCreateCart() {
        User user = getCurrentUser();
        if (user.getCart() == null) {
            Cart cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
            return cartRepository.save(cart);
        }
        return user.getCart();
    }

    private Cart getExistingCart() {
        Cart cart = getCurrentUser().getCart();
        if (cart == null) {
            throw new BusinessException(ErrorCodes.CART_EMPTY);
        }
        return cart;
    }

    private Book findBook(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));
    }

    private CartItem findCartItem(Cart cart, Integer bookId) {
        return cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);
    }

    private CartItem findCartItemOrThrow(Cart cart, Integer bookId) {
        return findCartItem(cart, bookId);
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .name(item.getBook().getNameBook())
                .price(item.getBook().getPrice())
                .total(item.getBook().getPrice() * item.getQuantity())
                .quantity(item.getQuantity())
                .coverImage(item.getBook().getImageCover().getImageUrl())
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND));
    }
}
