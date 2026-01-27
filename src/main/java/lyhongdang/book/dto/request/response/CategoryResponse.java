package lyhongdang.book.dto.request.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "CategoryResponse", description = "Category data returned by the API")
public class CategoryResponse {

    @Schema(description = "Category name", example = "Fiction")
    private String name;
}
