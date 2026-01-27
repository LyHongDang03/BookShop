package lyhongdang.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.response.OrderDetailResponse;
import lyhongdang.book.service.OrderDetailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderdetails")
@RequiredArgsConstructor
@Tag(name = "Order Detail API")
@SecurityRequirement(name = "bearerAuth")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @Operation(summary = "Get order detail item", description = "Retrieve a specific order detail item (only accessible by the owner of the order).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order detail returned successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized to access this order detail", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order detail not found", content = @Content)
    })
    @GetMapping("/{id}")
    public OrderDetailResponse orderDetail(@PathVariable int id) {
        return orderDetailService.getOrderDetailById(id);
    }
}
