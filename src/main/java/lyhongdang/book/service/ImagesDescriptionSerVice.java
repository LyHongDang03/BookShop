package lyhongdang.book.service;


import lombok.RequiredArgsConstructor;
import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.ImagesDescription;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.BookRepository;
import lyhongdang.book.repository.ImagesDescriptionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<String> uploadImage(MultipartFile[] files,
                                    String folder,
                                    Integer bookID) throws IOException {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));

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

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<String> updateImage(MultipartFile[] files,
                                    String folder,
                                    List<Integer> imageToDelete) throws IOException {
        if (files.length != imageToDelete.size()) {
            throw new BusinessException(ErrorCodes.IMAGE_NOT_ALLOWED);
        }
        List<String> fileUrl = new ArrayList<>();
        for (int i = 0; i < imageToDelete.size(); i++) {
            Integer imageID = imageToDelete.get(i);
            MultipartFile file = files[i];
            ImagesDescription imagesDescription = imageRepository.findById(imageID)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteImage(Integer imageID) throws IOException {
        ImagesDescription image = imageRepository.findById(imageID)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));
        fileSerVice.deleteFile(image.getPublicId());
        imageRepository.delete(image);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<String> getImagesByBookId(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));

        List<ImagesDescription> images = imageRepository.findByBook(book);

        if (images.isEmpty()) {
            throw new BusinessException(ErrorCodes.IMAGE_NOT_FOUND);
        }

        return images.stream()
                .map(ImagesDescription::getImageUrl)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public String getImageById(Integer imageId) {
        ImagesDescription image = imageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.IMAGE_NOT_FOUND));

        return image.getImageUrl();
    }

}