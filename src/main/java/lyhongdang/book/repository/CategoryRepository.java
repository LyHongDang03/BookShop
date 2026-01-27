package lyhongdang.book.repository;

import lyhongdang.book.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByNameCategory(String nameCategory);
}
