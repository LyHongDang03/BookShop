package lyhongdang.book.controller;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.AdminChatRequest;
import lyhongdang.book.dto.request.UserChatRequest;
import lyhongdang.book.dto.request.response.ChatMessageResponse;
import lyhongdang.book.entity.ChatMessage;
import lyhongdang.book.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/user/send")
    public ResponseEntity<ChatMessageResponse> sendMessageFromUser(
            @RequestBody UserChatRequest request) {
        ChatMessageResponse response = chatService.sendMessageFromUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/send")
    public ResponseEntity<ChatMessageResponse> sendMessageFromAdmin(
            @RequestBody AdminChatRequest request) {
        ChatMessageResponse response = chatService.sendMessageFromAdmin(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory() {
        List<ChatMessage> history = chatService.getChatHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChatMessage>> searchMessages(
            @RequestParam Integer userId,
            @RequestParam String keyword) {
        List<ChatMessage> messages = chatService.searchMessages(userId, keyword);
        return ResponseEntity.ok(messages);
    }
}
