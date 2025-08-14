package com.example.book.image.imagesDescription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImagesDescriptionController {
    private final ImagesDescriptionSerVice imageSerVice;

    @PostMapping("/upload-images/{bookID}")
    public ResponseEntity<List<String>> uploadImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("folder") String folder,
            @PathVariable("bookID") Integer bookID) throws IOException {
        return ResponseEntity.ok().body(imageSerVice.uploadImage(files,folder, bookID));
    }

    @PutMapping("/update")
    public ResponseEntity<List<String>> updateImages(
            @RequestParam(name = "files", required = false) MultipartFile[] files,
            @RequestParam("folder") String folder,
            @RequestParam(name = "imageToDelete", required = false) List<Integer> imageIdsToDelete
    ) throws IOException {
        if (imageIdsToDelete == null) {
            imageIdsToDelete = List.of();
        }
        List<String> uploaded = imageSerVice.updateImage(files,folder, imageIdsToDelete);
        return ResponseEntity.ok(uploaded);
    }
}
