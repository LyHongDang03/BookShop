package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {

    @Schema(
            description = "Name of the category",
            example = "Science Fiction"
    )
    @NotBlank(message = "Category name cannot be empty")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;
}
