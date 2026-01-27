package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.ImageCover;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.BookRepository;
import lyhongdang.book.repository.ImageCoverRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    public String uploadImageCover(MultipartFile file, String folder, Integer bookId) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));

        if (imageCoverRepository.findByBook(book).isPresent()) {
            throw new BusinessException(ErrorCodes.IMAGE_NOT_FOUND);
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    public String updateCover(MultipartFile file, String folder, Integer coverId) throws IOException {

        ImageCover oldCover = imageCoverRepository.findById(coverId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.IMAGE_NOT_FOUND));

        fileSerVice.deleteFile(oldCover.getPublicId());

        Map<?, ?> uploadResult = fileSerVice.uploadFile(file, folder);
        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        oldCover.setPublicId(publicId);
        oldCover.setImageUrl(imageUrl);

        imageCoverRepository.save(oldCover);
        return imageUrl;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteCover(Integer coverId) throws IOException {
        ImageCover imageCover = imageCoverRepository.findById(coverId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.IMAGE_NOT_FOUND));
        fileSerVice.deleteFile(imageCover.getPublicId());
        imageCoverRepository.delete(imageCover);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public String getCoverByBookId(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.BOOK_NOT_FOUND));

        ImageCover cover = imageCoverRepository.findByBook(book)
                .orElseThrow(() -> new BusinessException(ErrorCodes.IMAGE_NOT_FOUND));

        return cover.getImageUrl();
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String getCoverById(Integer coverId) {
        ImageCover cover = imageCoverRepository.findById(coverId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.IMAGE_NOT_FOUND));

        return cover.getImageUrl();
    }


}