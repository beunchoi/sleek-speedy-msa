package com.hanghae.orderservice.domain.order.scheduler;

import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class OrderScheduler {
  private final OrderRepository orderRepository;

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
  @Transactional
  public void completeReturn() {
    List<Order> requestedOrders = orderRepository.findAllByStatus(
        OrderStatus.RETURN_REQUESTED);

    for (Order order : requestedOrders) {
      if (order.getStatus() == OrderStatus.RETURN_REQUESTED &&
          LocalDate.now().isAfter(order.getReturnRequestedDate().plusDays(1))) {
        order.completeReturn();
//        product.incrementStock();
      } else {
        throw new IllegalStateException("반품 처리 조건이 맞지 않습니다.");
      }
    }
  }

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
  @Transactional
  public void updateOrderStatus() {
    List<Order> orderList = orderRepository.findAllByStatus(
        OrderStatus.ORDERED);

    if (orderList != null && !orderList.isEmpty()) {
      for (Order order : orderList) {
        order.orderedToShipped();
      }
    }

    List<Order> shippedOrderList = orderRepository.findAllByStatus(
        OrderStatus.SHIPPED);

    if (shippedOrderList != null && !shippedOrderList.isEmpty()) {
      for (Order order : shippedOrderList) {
        order.shippedToDelivered();
      }
    }
  }
}
