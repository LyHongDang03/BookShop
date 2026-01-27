package lyhongdang.book.dto.request;

import lombok.Data;

@Data
public class AdminChatRequest {
    private Integer userId;
    private String content;
}
