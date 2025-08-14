package com.example.book.book;

import com.example.book.category.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookRequest {
    private String nameBook;
    private String author;
    private String isbn;
    private String description;
    private String publisher;
    private int year;
    private int pages;
    private String owner;
    private Double price;
    private List<Integer> categories;

}
