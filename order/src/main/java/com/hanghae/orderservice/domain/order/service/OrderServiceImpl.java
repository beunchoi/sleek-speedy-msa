package com.hanghae.orderservice.domain.order.service;

import com.hanghae.orderservice.common.dto.ProductResponseDto;
import com.hanghae.orderservice.common.exception.order.InvalidOrderStatusException;
import com.hanghae.orderservice.common.exception.order.OrderForbiddenException;
import com.hanghae.orderservice.common.exception.order.OrderNotFoundException;
import com.hanghae.orderservice.common.exception.product.ProductNotFoundException;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.dto.ReturnResponseDto;
import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import com.hanghae.orderservice.domain.order.event.OrderCreatedEvent;
import com.hanghae.orderservice.domain.order.event.OrderFailedEvent;
import com.hanghae.orderservice.domain.order.event.PaymentFailedEvent;
import com.hanghae.orderservice.domain.order.event.StockCheckEvent;
import com.hanghae.orderservice.domain.order.producer.OrderEventProducer;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final ProductFetchService productFetchService;
  private final OrderEventProducer orderEventProducer;

  private static final int RETURN_PERIOD_DAYS = 2;

  @Transactional
  @Override
  public void createOrder(StockCheckEvent event) {
    // 주문 데이터 저장
    try {
      Order savedOrder = orderRepository.save(new Order(event));

      int totalPrice = savedOrder.getPrice() * savedOrder.getQuantity();
      orderEventProducer.publish(new OrderCreatedEvent(savedOrder.getOrderId(),
          event.getPaymentMethodId(), totalPrice,
          savedOrder.getProductId(), savedOrder.getQuantity()));
    } catch (Exception e) {
      log.error("주문 프로세스 중 에러 발생, message={}", e.getMessage());
      rollbackOrder(new PaymentFailedEvent(
          event.getProductId(), event.getOrderId(), event.getQuantity()));
    }
  }

  @Override
  public ProductResponseDto checkProduct(String productId) {
    ProductResponseDto product = productFetchService.getProductByProductId(productId);

    if (product == null) {
      throw new ProductNotFoundException("상품을 찾을 수 없습니다.");
    }

    return product;
  }

  @Override
  public List<OrderResponseDto> getOrdersByUserId(String userId) {
    List<Order> orderList = orderRepository.findAllByUserId(userId);
    List<OrderResponseDto> responseDtos = new ArrayList<>();

    for (Order order : orderList) {
      responseDtos.add(new OrderResponseDto(order));
    }

    return responseDtos;
  }

  @Transactional
  @Override
  public OrderResponseDto cancelOrder(String userId, String orderId) {
    Order order = validateAndGetOrder(userId, orderId);

    if (!order.getStatus().equals(OrderStatus.ORDERED)) {
      throw new InvalidOrderStatusException("배송 전의 상품만 취소할 수 있습니다.");
    }

    order.cancel();
    productFetchService.increaseProductStock(order.getProductId(), order.getQuantity());

    return new OrderResponseDto(order);
  }

  @Transactional
  @Override
  public ReturnResponseDto requestProductReturn(String userId, String orderId) {
    Order order = validateAndGetOrder(userId, orderId);

    // 주문 상태가 배송 완료이고 배송일 다음날까지만 반품 처리 가능
    if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
      throw new InvalidOrderStatusException("배송 완료 상태가 아닙니다.");
    }

    if (!LocalDate.now().isBefore(order.getDeliveredDate().plusDays(RETURN_PERIOD_DAYS))) {
      throw new InvalidOrderStatusException("반품 가능한 기간이 지났습니다.");
    }

    order.requestProductReturn();

    return new ReturnResponseDto(order);
  }

//  @Transactional
//  public OrderResponseDto updateStatusToFail(String orderId) {
//    Order order = findOrderByOrderId(orderId);
//
//    order.failure();
//    return new OrderResponseDto(order);
//  }
//
//  @Transactional
//  public OrderResponseDto updateStatusToSuccess(String orderId) {
//    Order order = findOrderByOrderId(orderId);
//
//    order.success();
//    return new OrderResponseDto(order);
//  }

  @Transactional
  @Override
  public void rollbackOrder(PaymentFailedEvent event) {
    try {
      Order order = findOrderByOrderId(event.getOrderId());
      order.failure();

      orderEventProducer.publishFailEvent(
          new OrderFailedEvent(event.getProductId(), event.getQuantity()));
    } catch (Exception e) {
      // dlq?
    }
  }

  @Override
  public Order validateAndGetOrder(String userId, String orderId) {
    Order order = findOrderByOrderId(orderId);

    if (!order.getUserId().equals(userId)) {
      throw new OrderForbiddenException("사용자의 주문이 아닙니다.");
    }
    return order;
  }

  @Override
  public Order findOrderByOrderId(String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new OrderNotFoundException("해당 주문이 존재하지 않습니다."));
    return order;
  }

}
