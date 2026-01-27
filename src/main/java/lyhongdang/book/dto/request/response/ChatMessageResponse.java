package lyhongdang.book.dto.request.response;

import lombok.Data;

import java.time.Instant;

@Data
public class ChatMessageResponse {
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private Instant timestamp;
}
