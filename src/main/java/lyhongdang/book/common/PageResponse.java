package lyhongdang.book.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    @Schema(description = "Page number", example = "1")
    private int number;
    @Schema(description = "Page size", example = "10")
    private int size;
    @Schema(description = "Total elements", example = "1")
    private long totalElements;
    @Schema(description = "Total pages", example = "1")
    private int totalPages;
    private boolean first;
    private boolean last;
}
