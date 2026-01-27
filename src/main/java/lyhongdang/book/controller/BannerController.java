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
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.BannerRequest;
import lyhongdang.book.dto.request.response.BannerPageResponse;
import lyhongdang.book.dto.request.response.BannerResponse;
import lyhongdang.book.service.BannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
@Tag(name = "Banner API")
@SecurityRequirement(name = "bearerAuth")
public class BannerController {

    private final BannerService bannerService;

    @PostMapping
    @Operation(summary = "Create banner", description = "Create a new banner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Banner created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BannerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden" , content = @Content)
    })
    public ResponseEntity<BannerResponse> createBanner(
            @Parameter(example = "banner") @RequestParam("folder") String folder,
            @Parameter(example = "HoangThiBichNgoc.img") @RequestParam("file") MultipartFile file,
            @ModelAttribute BannerRequest request) throws Exception {
             var result = bannerService.createBanner(folder, file, request);
             return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete banner", description = "Delete a banner by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Banner deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized" , content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Banner not found" , content = @Content)
    })
    public ResponseEntity<Void> deleteBanner(@Schema(description = "Banner id", example = "1") @PathVariable("id") Integer id) throws Exception {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update banner", description = "Update a banner by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Banner updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BannerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized" , content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden" , content = @Content),
            @ApiResponse(responseCode = "404", description = "Banner not found" , content = @Content)
    })
    public ResponseEntity<BannerResponse> updateBanner(
          @Parameter(description = "Banner id", example = "1")  @PathVariable("id") Integer id,
          @Parameter(example = "banner") @RequestParam("folder") String folder,
          @Parameter(example = "HoangThiBichNgoc.img")  @RequestParam("file") MultipartFile file) throws Exception {
        var result = bannerService.updateBanner(file, folder, id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get banner by id", description = "Get banner details by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List banner ",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BannerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized" , content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden" , content = @Content),
            @ApiResponse(responseCode = "404", description = "Banner not found" , content = @Content)
    })
    public ResponseEntity<BannerResponse> getBannerById(@Parameter(description = "Banner id",example = "1") @PathVariable("id") Integer id) {
        return ResponseEntity.ok(bannerService.getBannerById(id));
    }

    @GetMapping
    @Operation(summary = "Get all banners", description = "Get all banners")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of banners",
                    content = @Content(schema = @Schema(implementation = BannerPageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content())
    })
    public ResponseEntity<PageResponse<BannerResponse>> getAllBanner(
            @Parameter(description = "Page number (default = 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (default = 10)") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bannerService.getAllBanner(page, size));
    }
}
