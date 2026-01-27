package lyhongdang.book.repository;

import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.ImagesDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagesDescriptionRepository extends JpaRepository<ImagesDescription, Integer> {
    List<ImagesDescription> findByBook(Book book);
}
