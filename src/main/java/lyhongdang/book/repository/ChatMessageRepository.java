package lyhongdang.book.repository;

import lyhongdang.book.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    @Query("{ $or: [ { $and: [ {'senderId': ?0}, {'receiverId': ?1} ] }, { $and: [ {'senderId': ?1}, {'receiverId': ?0} ] } ] }")
    List<ChatMessage> findChatBetween(Integer userId, Integer adminId);

    @Query("{ $or: [ {'senderId': ?0}, {'receiverId': ?0} ], 'content': { $regex: ?1, $options: 'i' } }")
    List<ChatMessage> findByUserIdAndContent(Integer userId, String keyword);
}
