package com.example.book.image.imageCover;

import com.example.book.book.Book;
import com.example.book.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class ImageCover extends BaseEntity {
    private String imageUrl;
    private String publicId;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
