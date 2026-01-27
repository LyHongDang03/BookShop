package lyhongdang.book.openlibraryapi;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.response.BookResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/openlibrary")
@RequiredArgsConstructor
public class OpenLibraryController {

    private final OpenLibraryService openLibraryService;

    @GetMapping("/search")
    public List<SearchBookResponse> searchBooks(@RequestParam String title) {
        return openLibraryService.searchBooksByTitle(title);
    }

    @PostMapping("/import")
    public BookResponse importBook(@RequestBody ImportBookRequest request) {
        return openLibraryService.importBook(request);
    }
}