package lyhongdang.book.repository;

import lyhongdang.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findDistinctByCategories_IdIn(List<Integer> categoryIds, Pageable pageable);
    boolean existsByIsbn(String isbn);
}
