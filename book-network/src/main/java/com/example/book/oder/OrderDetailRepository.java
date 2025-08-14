package com.example.book.oder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
   Optional<OrderDetail>findByOrderId(int orderId);
}
