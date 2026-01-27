package lyhongdang.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.response.ViewCartResponse;
import lyhongdang.book.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Cart API")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    /* =======================================================
     *                ADD TO CART
     * ======================================================= */
    @PostMapping
    @Operation(summary = "Add to cart", description = "Add a book to the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added to cart successfully"),
            @ApiResponse(responseCode = "400", description = "Not enough stock for the book", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public String addToCart(
            @Parameter(description = "Book ID to add", example = "1") @RequestParam Integer bookId,
            @Parameter(description = "Quantity of the book", example = "2") @RequestParam Integer quantity) {
        cartService.addToCart(bookId, quantity);
        return "Book added to cart successfully";
    }

    /* =======================================================
     *                VIEW CART
     * ======================================================= */
    @GetMapping
    @Operation(summary = "View cart", description = "Retrieve the current cart of the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart details retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ViewCartResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ViewCartResponse viewCart() {
        return cartService.viewCart();
    }

    /* =======================================================
     *                UPDATE CART
     * ======================================================= */
    @PutMapping("/{bookId}")
    @Operation(summary = "Update cart", description = "Update the quantity of a specific book in the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart updated successfully"),
            @ApiResponse(responseCode = "400", description = "Cart is empty", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart item not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public String updateCart(
            @Parameter(description = "Book ID to update", example = "1") @PathVariable Integer bookId,
            @Parameter(description = "New quantity for the book", example = "3") @RequestParam Integer quantity) {
        cartService.updateCart(bookId, quantity);
        return "Cart updated successfully";
    }

    /* =======================================================
     *                REMOVE FROM CART
     * ======================================================= */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "Remove from cart", description = "Remove a specific book from the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book removed from cart successfully"),
            @ApiResponse(responseCode = "400", description = "Cart is empty", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart item not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public String removeFromCart(
            @Parameter(description = "Book ID to remove", example = "1") @PathVariable Integer bookId) {
        cartService.removeFromCart(bookId);
        return "Book removed from cart successfully";
    }
}
