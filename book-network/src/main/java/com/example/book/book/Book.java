package com.example.book.book;

import com.example.book.cartItem.CartItem;
import com.example.book.category.Category;
import com.example.book.common.BaseEntity;

import com.example.book.image.imageCover.ImageCover;
import com.example.book.image.imagesDescription.ImagesDescription;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity {
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
    @ManyToMany
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ImagesDescription> images;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private ImageCover imageCover;

}
