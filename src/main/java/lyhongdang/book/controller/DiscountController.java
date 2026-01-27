package lyhongdang.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.DiscountRequest;
import lyhongdang.book.dto.request.UpdateDiscountRequest;
import lyhongdang.book.dto.request.response.DiscountResponse;
import lyhongdang.book.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@Tag(name = "Discounts API")
@SecurityRequirement(name = "bearerAuth")
public class DiscountController {

    private final DiscountService discountService;

    /* =======================================================
     *                CREATE DISCOUNT
     * ======================================================= */
    @PostMapping
    @Operation(summary = "Create discount", description = "Creates a new discount. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiscountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., duplicate code)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<DiscountResponse> create(@RequestBody @Valid DiscountRequest request) {
        return ResponseEntity.ok(discountService.createDiscount(request));
    }

    /* =======================================================
     *                UPDATE DISCOUNT
     * ======================================================= */
    @PutMapping("/{id}")
    @Operation(summary = "Update discount", description = "Updates an existing discount by its ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiscountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Discount not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<DiscountResponse> update(
            @Parameter(description = "Discount ID", example = "1", required = true)
            @PathVariable("id") Integer discountId,
            @RequestBody @Valid UpdateDiscountRequest request) {
        return ResponseEntity.ok(discountService.updateDiscount(discountId, request));
    }

    /* =======================================================
     *                GET DISCOUNT BY ID
     * ======================================================= */
    @GetMapping("/{id}")
    @Operation(summary = "Get discount details", description = "Retrieves a discount by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DiscountResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Discount not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<DiscountResponse> getById(
            @Parameter(description = "Discount ID", example = "1", required = true)
            @PathVariable("id") Integer discountId) {
        return ResponseEntity.ok(discountService.getDiscount(discountId));
    }

    /* =======================================================
     *                GET ALL DISCOUNTS
     * ======================================================= */
    @GetMapping
    @Operation(summary = "List discounts", description = "Returns a paginated list of discounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discounts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<PageResponse<DiscountResponse>> getAll(
            @Parameter(description = "Page index (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discountService.getAll(page, size));
    }

    /* =======================================================
     *                DELETE DISCOUNT
     * ======================================================= */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete discount", description = "Deletes a discount by its ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Discount not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> delete(
            @Parameter(description = "Discount ID", example = "1", required = true)
            @PathVariable("id") Integer discountId) {
        return ResponseEntity.ok(discountService.deleteDiscount(discountId));
    }

    /* =======================================================
     *                ACTIVATE DISCOUNT
     * ======================================================= */
    @PatchMapping("/{id}/active")
    @Operation(summary = "Activate discount", description = "Activates a discount by its ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discount activated successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Discount not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> active(
            @Parameter(description = "Discount ID", example = "1", required = true)
            @PathVariable("id") Integer discountId) {
        return ResponseEntity.ok(discountService.activeDiscount(discountId));
    }
}
