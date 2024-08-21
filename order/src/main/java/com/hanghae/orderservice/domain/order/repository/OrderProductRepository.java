package com.hanghae.orderservice.domain.order.repository;

import com.hanghae.orderservice.domain.order.entity.OrderProduct;
import com.hanghae.orderservice.domain.order.entity.OrderProductStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

  List<OrderProduct> findAllByOrderId(Long id);

  List<OrderProduct> findAllByStatus(OrderProductStatus orderProductStatus);
}
