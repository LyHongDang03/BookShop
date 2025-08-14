package com.example.book.image.imageCover;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageCoverController {
    private final ImageCoverService imageCoverserVice;

    @PostMapping("/upload/{bookId}")
    public ResponseEntity<String> uploadImageCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder,
            @PathVariable("bookId") Integer bookId) {
        try {
            String fileName = imageCoverserVice.uploadImageCover(file, folder, bookId);
            return ResponseEntity.ok("Uploaded file: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
    @PutMapping("/update")
    public ResponseEntity<String> updateCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder,
            @RequestParam("deleteCoverId") Integer coverId
          ) {
        try {
            String fileName = imageCoverserVice.updateCover(file, folder, coverId);
            return ResponseEntity.ok("Updated with new file: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Update failed: " + e.getMessage());
        }
    }

}
