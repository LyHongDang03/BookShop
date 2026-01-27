package lyhongdang.book.dto.request.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerResponse {
    @Schema(description = "Name of the banner", example = "Banner 1")
    private String name;
    @Schema(description = "Image URL of the banner", example = "https://example.com/image.jpg")
    private String imageURL;
    @Schema(description = "Active", example = "true")
    private boolean active;
}
