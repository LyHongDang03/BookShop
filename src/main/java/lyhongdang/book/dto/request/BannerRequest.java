package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerRequest {
    @Schema(description = "Name of banner", example = "Banner 1")
    private String name;
    @Schema(description = "Active", example = "true")
    private Boolean active;
}
