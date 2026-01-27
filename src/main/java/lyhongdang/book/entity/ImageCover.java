package lyhongdang.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lyhongdang.book.common.BaseEntity;

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
