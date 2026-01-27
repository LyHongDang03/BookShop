package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {
    @Schema(description = "Token of user", example = "123456")
    private String token;
    @Schema(description = "Password of user", example = "Password")
    private String password;
}
