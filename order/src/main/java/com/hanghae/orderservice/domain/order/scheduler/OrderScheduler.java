package com.hanghae.orderservice.domain.order.scheduler;

import com.hanghae.orderservice.domain.order.client.ProductServiceClient;
import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderScheduler {

  private final OrderRepository orderRepository;
  private final ProductServiceClient productServiceClient;

  private static final int RETURN_PROCESSING_DAYS = 1;

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
  @Transactional
  public void completeProductReturn() {
    List<Order> requestedOrders = orderRepository.findAllByStatus(
        OrderStatus.RETURN_REQUESTED);

    for (Order order : requestedOrders) {
      // ex) 23일에 반품 신청하면 25일 자정에 반품 완료 처리
      if (order.getStatus() == OrderStatus.RETURN_REQUESTED &&
          LocalDate.now().isAfter(order.getReturnRequestedDate().plusDays(RETURN_PROCESSING_DAYS))) {
        productServiceClient.increaseProductStock(order.getProductId(), order.getQuantity());
        order.completeProductReturn();
      }
    }
  }

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
  @Transactional
  public void updateOrderStatus() {
    List<Order> shippedOrderList = orderRepository.findAllByStatus(
        OrderStatus.SHIPPED);

    for (Order order : shippedOrderList) {
      order.shippedToDelivered();
    }

    List<Order> orderList = orderRepository.findAllByStatus(
        OrderStatus.ORDERED);

    for (Order order : orderList) {
      order.orderedToShipped();
    }
  }

}
