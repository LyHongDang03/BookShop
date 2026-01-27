package lyhongdang.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.CheckoutRequest;
import lyhongdang.book.dto.request.response.OrderResponse;
import lyhongdang.book.dto.request.response.OrderStatsResponse;
import lyhongdang.book.dto.request.response.ViewOrderResponse;
import lyhongdang.book.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order API")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Checkout", description = "Place an order with selected cart items and apply discount if available.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout successful"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or no items selected", content = @Content),
            @ApiResponse(responseCode = "404", description = "Discount code or book not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Book is out of stock", content = @Content)
    })
    @PostMapping("/checkout")
    public ResponseEntity<String> checkOut(@RequestBody CheckoutRequest request) {
        orderService.checkoutOffline(request);
        return ResponseEntity.ok("Checkout successful");
    }

    @Operation(summary = "Get orders (paginated)", description = "Retrieve a paginated list of orders for the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders returned successfully")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ViewOrderResponse>> getOrdersByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getOrdersByPage(page, size));
    }

    @Operation(summary = "Get order detail", description = "Retrieve detailed information of a specific order (only accessible by the owner).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order detail returned successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to access this order", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable int id) {
        var rs = orderService.getOrderById(id);
        return ResponseEntity.ok(rs);
    }

    @Operation(summary = "Get today's order stats", description = "Retrieve total orders and revenue for today.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Today's stats returned successfully")
    })
    @GetMapping("/stats/today")
    public ResponseEntity<OrderStatsResponse> getTodayStats() {
        return ResponseEntity.ok(orderService.getTodayStats());
    }

    @Operation(summary = "Get order stats by date", description = "Retrieve total orders and revenue for a specific date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stats for the selected date returned successfully")
    })
    @GetMapping("/stats")
    public ResponseEntity<OrderStatsResponse> getStatsByDate(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderService.getStatsByDate(date));
    }
}
