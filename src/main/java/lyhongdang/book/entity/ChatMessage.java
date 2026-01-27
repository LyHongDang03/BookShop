package lyhongdang.book.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "messages")
public class ChatMessage {
    @Id
    private String id;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private Instant timestamp = Instant.now();
}
