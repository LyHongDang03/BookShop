package lyhongdang.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.service.ImageCoverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image-cover")
@RequiredArgsConstructor
@Tag(name = "Image Cover API")
@SecurityRequirement(name = "bearerAuth")
public class ImageCoverController {

    private final ImageCoverService imageCoverService;

    @PostMapping("/{bookId}")
    @Operation(summary = "Upload cover image", description = "Upload a cover image for a specific book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Cover already exists", content = @Content)
    })
    public ResponseEntity<String> uploadCover(
            @Parameter(description = "Book ID to attach the cover", example = "1") @PathVariable Integer bookId,
            @Parameter(description = "Image file") @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "books/covers") String folder
    ) throws IOException {
        return ResponseEntity.ok(
                imageCoverService.uploadImageCover(file, folder, bookId)
        );
    }

    @PutMapping("/{coverId}")
    @Operation(summary = "Update cover image", description = "Replace an existing cover image with a new one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cover not found", content = @Content)
    })
    public ResponseEntity<String> updateCover(
            @Parameter(description = "Cover ID to update", example = "1") @PathVariable Integer coverId,
            @Parameter(description = "New image file") @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "books/covers") String folder
    ) throws IOException {
        return ResponseEntity.ok(
                imageCoverService.updateCover(file, folder, coverId)
        );
    }

    @DeleteMapping("/{coverId}")
    @Operation(summary = "Delete cover image", description = "Delete a cover image by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cover not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCover(
            @Parameter(description = "Cover ID to delete", example = "1") @PathVariable Integer coverId
    ) throws IOException {
        imageCoverService.deleteCover(coverId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get cover by book ID", description = "Retrieve cover image URL of a specific book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cover retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book or cover not found", content = @Content)
    })
    public ResponseEntity<String> getCoverByBookId(
            @Parameter(description = "Book ID", example = "1") @PathVariable Integer bookId
    ) {
        return ResponseEntity.ok(
                imageCoverService.getCoverByBookId(bookId)
        );
    }

    @GetMapping("/{coverId}")
    @Operation(summary = "Get cover by ID", description = "Retrieve cover image URL by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cover retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Cover not found", content = @Content)
    })
    public ResponseEntity<String> getCoverById(
            @Parameter(description = "Cover ID", example = "1") @PathVariable Integer coverId
    ) {
        return ResponseEntity.ok(
                imageCoverService.getCoverById(coverId)
        );
    }
}
