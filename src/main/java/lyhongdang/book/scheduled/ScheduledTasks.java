package lyhongdang.book.scheduled;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.repository.TokenRepository;
import lyhongdang.book.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduledTasks  {
    private final TokenRepository tokenRepository;
    private final OrderService orderService;
    @Scheduled(cron = "0 59 23 ? * SUN")
    @Transactional
    public void cronJob() {
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
    @Scheduled(cron = "0 59 23 * * ?")
    @Transactional
    public void sendDailyReportJob() {
        orderService.sendTodayReport("lyhongdang03@gmail.com");
    }
}