package com.example.book.image.imageCover;

import com.example.book.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageCoverRepository extends JpaRepository<ImageCover, Integer> {
    Optional<ImageCover> findByBook(Book book);
}
