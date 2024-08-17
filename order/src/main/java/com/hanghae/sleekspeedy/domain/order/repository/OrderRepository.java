package com.hanghae.sleekspeedy.domain.order.repository;

import com.hanghae.sleekspeedy.domain.order.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByIdAndUserId(Long orderId, Long id);
  Optional<Order> findByOrderId(String orderId);
  List<Order> findByUserId(String userId);
}
