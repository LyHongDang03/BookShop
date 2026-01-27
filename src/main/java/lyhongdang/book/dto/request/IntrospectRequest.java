package lyhongdang.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntrospectRequest {
    @Schema(description = "Token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJseWhvbmdkYW5nMDNAZ21haWwuY29tIiwic3ViIjoibHlob25nZGFuZzAzQGdtYWlsLmNvbSIsImV4cCI6MzAxNzU4NzA5Mzg0LCJpYXQiOjE3NTg3MDkzODQsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiIsIlJFQUQiXX0.lVd2ShODkdXAKSNuJ1JeA0W-xuZtHnVcixXI2DhPLw3wZWQnpPhC6Gpzobb6KImJZon5VUUmVyXYL3w0BDcErA")
    private String token;
}
