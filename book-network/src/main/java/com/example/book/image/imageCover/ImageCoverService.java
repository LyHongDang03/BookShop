package com.example.book.image.imageCover;

import com.example.book.book.Book;
import com.example.book.book.BookRepository;
import com.example.book.file.FileSerVice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageCoverService {

    private final BookRepository bookRepository;
    private final FileSerVice fileSerVice;
    private final ImageCoverRepository imageCoverRepository;

    public String uploadImageCover(MultipartFile file, String folder, Integer bookId) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (imageCoverRepository.findByBook(book).isPresent()) {
            throw new RuntimeException("Book already has a cover. Use update instead.");
        }

        Map<?, ?> uploadResult = fileSerVice.uploadFile(file, folder);
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        ImageCover imageCover = new ImageCover();
        imageCover.setPublicId(publicId);
        imageCover.setImageUrl(imageUrl);
        imageCover.setBook(book);

        imageCoverRepository.save(imageCover);
        return imageUrl;
    }

    public String updateCover(MultipartFile file, String folder, Integer coverId) throws IOException {

        ImageCover oldCover = imageCoverRepository.findById(coverId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        fileSerVice.deleteFile(oldCover.getPublicId());

        Map<?, ?> uploadResult = fileSerVice.uploadFile(file, folder);
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        oldCover.setPublicId(publicId);
        oldCover.setImageUrl(imageUrl);

        imageCoverRepository.save(oldCover);
        return imageUrl;
    }

    public void deleteCover(Integer coverId) throws IOException {
        ImageCover imageCover = imageCoverRepository.findById(coverId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        fileSerVice.deleteFile(imageCover.getPublicId());
        imageCoverRepository.delete(imageCover);
    }
}