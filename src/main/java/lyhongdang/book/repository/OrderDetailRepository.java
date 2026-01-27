package lyhongdang.book.repository;

import lyhongdang.book.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
   Optional<OrderDetail>findByOrderId(int orderId);
    @Query("SELECT od FROM OrderDetail od " +
            "JOIN FETCH od.order o " +
            "JOIN FETCH o.user u " +
            "WHERE od.id = :id")
    Optional<OrderDetail> findByIdWithOrderAndUser(@Param("id") int id);
}
