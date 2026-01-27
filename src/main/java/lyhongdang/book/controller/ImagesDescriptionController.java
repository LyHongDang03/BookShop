package lyhongdang.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.service.ImagesDescriptionSerVice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images-description")
@RequiredArgsConstructor
@Tag(name = "Images Description API")
@SecurityRequirement(name = "bearerAuth")
public class ImagesDescriptionController {

    private final ImagesDescriptionSerVice imagesDescriptionSerVice;

    @Operation(summary = "Upload images for a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Image not allowed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    @PostMapping("/{bookId}")
    public ResponseEntity<List<String>> uploadImages(
            @PathVariable Integer bookId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(defaultValue = "books/descriptions") String folder
    ) throws IOException {
        return ResponseEntity.ok(
                imagesDescriptionSerVice.uploadImage(files, folder, bookId)
        );
    }

    @Operation(summary = "Update images for a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images updated successfully"),
            @ApiResponse(responseCode = "400", description = "Image not allowed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    @PutMapping()
    public ResponseEntity<List<String>> updateImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(defaultValue = "books/descriptions") String folder,
            @RequestParam List<Integer> imageToDelete
    ) throws IOException {
        return ResponseEntity.ok(
                imagesDescriptionSerVice.updateImage(files, folder, imageToDelete)
        );
    }

    @Operation(summary = "Delete an image by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found", content = @Content)
    })
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer imageId) throws IOException {
        imagesDescriptionSerVice.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all images of a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found or no images", content = @Content)
    })
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<String>> getImagesByBookId(@PathVariable Integer bookId) {
        return ResponseEntity.ok(
                imagesDescriptionSerVice.getImagesByBookId(bookId)
        );
    }

    @Operation(summary = "Get an image by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found", content = @Content)
    })
    @GetMapping("/{imageId}")
    public ResponseEntity<String> getImageById(@PathVariable Integer imageId) {
        return ResponseEntity.ok(
                imagesDescriptionSerVice.getImageById(imageId)
        );
    }
}
