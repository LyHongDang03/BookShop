package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.LockUserRequest;
import lyhongdang.book.entity.User;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public void updateUserToken(String token, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND));
        if (user != null) {
            user.setRefreshToken(token);
            userRepository.save(user);
        }
    }

    public User getUserRefreshTokenAndEmail(String token, String email) {
        return userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public String lockUser(LockUserRequest request){
        var result = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND));
        result.setAccountLocked(request.isLock());
        userRepository.save(result);
        return "successfully";
    }
}
