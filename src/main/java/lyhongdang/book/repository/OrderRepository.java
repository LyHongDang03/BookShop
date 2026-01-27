package lyhongdang.book.repository;

import lyhongdang.book.entity.Order;
import lyhongdang.book.entity.User;
import lyhongdang.book.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    Page<Order> findByUser(User user, Pageable pageable);
    boolean existsByStatus(OrderStatus status);
}
