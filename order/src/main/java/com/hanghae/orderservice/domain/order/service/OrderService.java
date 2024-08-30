package com.hanghae.orderservice.domain.order.service;

import com.hanghae.orderservice.domain.order.dto.CancelResponse;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.dto.PaymentRequest;
import com.hanghae.orderservice.domain.order.dto.ReturnRequestResponse;
import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import com.hanghae.orderservice.global.messagequeue.KafkaProducer;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final KafkaProducer kafkaProducer;

  @Transactional
  public OrderResponseDto createOrder(OrderRequestDto request, String userId) {
    String orderId = UUID.randomUUID().toString();
    Integer totalPrice = request.getPrice() * request.getQuantity();
    OrderStatus status = OrderStatus.PENDING;

    Order order = orderRepository.save(new Order(request, orderId, totalPrice, userId, status));
    OrderResponseDto response = new OrderResponseDto(order);

    kafkaProducer.send("payment-topic", new PaymentRequest(response, userId));

    return response;
  }

  @Transactional
  public CancelResponse cancelOrder(String userId, String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("주문 상품이 존재하지 않습니다."));

    if (!order.getUserId().equals(userId)) {
      throw new IllegalStateException("주문을 취소할 수 없습니다.");
    }

    if (order.getStatus() == OrderStatus.ORDERED) {
      order.cancel();
//      product.incrementStock();
    } else {
      throw new IllegalStateException("배송 전의 상품만 취소할 수 있습니다.");
    }

    return new CancelResponse(order);
  }

  @Transactional
  public ReturnRequestResponse requestReturn(String userId, String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("주문 상품이 존재하지 않습니다."));

    if (!order.getUserId().equals(userId)) {
      throw new IllegalStateException("반품할 수 없습니다.");
    }

    if (order.getStatus() == OrderStatus.DELIVERED && LocalDate.now().isBefore(order.getDeliveredDate().plusDays(1))) {
      order.requestReturn();  // 반품 처리
    } else {
      throw new IllegalStateException("반품 가능한 기간이 지났거나, 배송 완료 상태가 아닙니다.");
    }

    return new ReturnRequestResponse(order);
  }

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

  public List<OrderResponseDto> getOrdersByUserId(String userId) {
    List<Order> orderList = orderRepository.findAllByUserId(userId);

    return orderList.stream().map(OrderResponseDto::new).collect(Collectors.toList());
  }
}
