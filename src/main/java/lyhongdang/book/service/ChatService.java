package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.AdminChatRequest;
import lyhongdang.book.dto.request.UserChatRequest;
import lyhongdang.book.dto.request.response.ChatMessageResponse;
import lyhongdang.book.entity.ChatMessage;
import lyhongdang.book.entity.User;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.ChatMessageRepository;
import lyhongdang.book.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private static final Integer ADMIN_ID = 1;

    public ChatMessageResponse sendMessageFromUser(UserChatRequest request) {
        // user gui tin nhan cho admin
        User user = getCurrentUser();
        ChatMessage message = new ChatMessage();
        message.setSenderId(user.getId());
        message.setReceiverId(ADMIN_ID);
        message.setContent(request.getContent());
        var result = chatMessageRepository.save(message);

        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setSenderId(result.getSenderId());
        chatMessageResponse.setReceiverId(result.getReceiverId());
        chatMessageResponse.setContent(result.getContent());
        chatMessageResponse.setTimestamp(result.getTimestamp());
        return chatMessageResponse;
    }

    public ChatMessageResponse sendMessageFromAdmin(AdminChatRequest request) {
        // admin gui tin nhan cho user
        User admin = getCurrentUser();
        Integer userId = request.getUserId();
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND));

        ChatMessage message = new ChatMessage();
        message.setSenderId(admin.getId());
        message.setReceiverId(userId);
        message.setContent(request.getContent());
        var result = chatMessageRepository.save(message);

        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setSenderId(result.getSenderId());
        chatMessageResponse.setReceiverId(result.getReceiverId());
        chatMessageResponse.setContent(result.getContent());
        chatMessageResponse.setTimestamp(result.getTimestamp());
        return chatMessageResponse;
    }

    public List<ChatMessage> getChatHistory() {
        User user = getCurrentUser();
        return chatMessageRepository.findChatBetween(user.getId(), ADMIN_ID);
    }

    public List<ChatMessage> searchMessages(Integer userId, String keyword) {
        return chatMessageRepository.findByUserIdAndContent(userId, keyword);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND));
    }
}
