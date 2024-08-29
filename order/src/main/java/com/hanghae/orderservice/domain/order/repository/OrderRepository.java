package com.hanghae.orderservice.domain.order.repository;

import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByOrderId(String orderId);
  List<Order> findAllByStatus(OrderStatus status);
  List<Order> findAllByUserId(String userId);
}
