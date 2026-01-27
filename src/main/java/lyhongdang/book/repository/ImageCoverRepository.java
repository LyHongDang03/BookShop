package lyhongdang.book.repository;

import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.ImageCover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageCoverRepository extends JpaRepository<ImageCover, Integer> {
    Optional<ImageCover> findByBook(Book book);
}
