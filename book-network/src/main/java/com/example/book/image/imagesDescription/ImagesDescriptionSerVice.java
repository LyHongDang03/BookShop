package com.example.book.image.imagesDescription;

import com.example.book.book.Book;
import com.example.book.book.BookRepository;
import com.example.book.file.FileSerVice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImagesDescriptionSerVice {
    private final ImagesDescriptionRepository imageRepository;
    private final BookRepository bookRepository;
    private final FileSerVice fileSerVice;

    public List<String> uploadImage(MultipartFile[] files,
                                    String folder,
                                    Integer bookID) throws IOException {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<String> uploadFileName = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                Map<?, ?> uploadResult = fileSerVice.uploadFile(file, folder);
                String imageUrl = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");

                ImagesDescription imagesDescription = new ImagesDescription();
                imagesDescription.setPublicId(publicId);
                imagesDescription.setImageUrl(imageUrl);
                imagesDescription.setBook(book);
                imageRepository.save(imagesDescription);

                uploadFileName.add(imageUrl);
            }
        }
        return uploadFileName;
    }

    public List<String> updateImage(MultipartFile[] files,
                                    String folder,
                                    List<Integer> imageToDelete) throws IOException {
        if (files.length != imageToDelete.size()) {
            throw new IllegalArgumentException("File number and image ID do not match");
        }
        List<String> fileUrl = new ArrayList<>();
        for (int i = 0; i < imageToDelete.size(); i++) {
            Integer imageID = imageToDelete.get(i);
            MultipartFile file = files[i];
            ImagesDescription imagesDescription = imageRepository.findById(imageID)
                    .orElseThrow(() -> new RuntimeException("Image not found"));
            fileSerVice.deleteFile(imagesDescription.getPublicId());

            Map<?, ?> uploadResult = fileSerVice.uploadFile(file, folder);
            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            imagesDescription.setImageUrl(imageUrl);
            imagesDescription.setPublicId(publicId);
            imageRepository.save(imagesDescription);
            fileUrl.add(imageUrl);
        }
        return fileUrl;
    }

    public void deleteImage(Integer imageID) throws IOException {
        ImagesDescription image = imageRepository.findById(imageID)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        fileSerVice.deleteFile(image.getPublicId());
        imageRepository.delete(image);
    }
}