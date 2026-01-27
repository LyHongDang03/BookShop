package lyhongdang.book.openlibraryapi;

import lombok.Data;

import java.util.List;

@Data
public class SearchBookResponse {
    private String title;
    private List<String> isbns;
    private List<String> authors;
}
