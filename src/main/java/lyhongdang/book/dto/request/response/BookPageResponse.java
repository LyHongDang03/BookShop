package lyhongdang.book.dto.request.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lyhongdang.book.common.PageResponse;

@Schema(description = "Page response chứa BookResponse")
public class BookPageResponse extends PageResponse<BookResponse> {
}
