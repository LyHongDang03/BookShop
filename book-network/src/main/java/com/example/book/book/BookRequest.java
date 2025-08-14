package com.example.book.book;

import com.example.book.category.Category;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    private String nameBook;
    private String author;
    private String isbn;
    private String description;
    private String publisher;
    private int year;
    private int pages;
    private String owner;
    private double price;
    private int quantity;
    private List<Integer> categories;
}
