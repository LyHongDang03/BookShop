package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockUserRequest {
    @Schema(description = "Email of user", example = "lyhongdang@gmail.com")
    private String email;
    @Schema(description = "Lock user", example = "true")
    private boolean lock;
}
