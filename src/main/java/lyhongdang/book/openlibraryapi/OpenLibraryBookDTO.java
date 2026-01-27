package lyhongdang.book.openlibraryapi;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OpenLibraryBookDTO {
    private String title;
    private List<String> publishers;
    private String publish_date;
    private Integer number_of_pages;
    private List<Map<String, String>> authors; // [{ "key": "/authors/OL12345A" }]
    private List<Map<String, String>> works;   // [{ "key": "/works/OL45804W" }]
    private List<String> subjects;
}
