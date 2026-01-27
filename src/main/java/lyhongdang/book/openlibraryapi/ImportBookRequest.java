package lyhongdang.book.openlibraryapi;

import lombok.Data;

@Data
public class ImportBookRequest {
    private String isbn;
    private String owner;
    private double price;
    private int quantity;
}
