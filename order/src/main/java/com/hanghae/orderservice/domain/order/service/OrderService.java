package com.hanghae.orderservice.domain.order.service;

import com.hanghae.orderservice.domain.order.entity.OrderProductStatus;
import com.hanghae.orderservice.domain.order.repository.OrderProductRepository;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderProduct;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderProductRepository orderProductRepository;

  public OrderResponseDto createOrder(OrderRequestDto request, String userId) {
    String orderId = UUID.randomUUID().toString();
    Integer totalPrice = request.getPrice() * request.getQuantity();

    Order order = orderRepository.save(new Order(request, orderId, totalPrice, userId));

    return new OrderResponseDto(order);
  }

  public OrderResponseDto getOrderByOrderId(String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new NullPointerException("주문이 존재하지 않습니다."));

    return new OrderResponseDto(order);
  }

  public List<OrderResponseDto> getOrdersByUserId(String userId) {
    List<Order> orders = orderRepository.findByUserId(userId);

    return orders.stream().map(OrderResponseDto::new).collect(Collectors.toList());
  }

//  public List<OrderProductResponse> getOrderProducts(Long orderId, User user) {
//    Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
//        .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
//
//    List<OrderProduct> productList = orderProductRepository.findAllByOrderId(order.getId());
//    return productList.stream().map(OrderProductResponse::new).collect(Collectors.toList());
//  }
//
//  @Transactional
//  public void cancelOrderProduct(Long orderProductId, User user) {
//    OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
//        .orElseThrow(() -> new IllegalArgumentException("주문 상품이 존재하지 않습니다."));
//
//    Order order = orderProduct.getOrder();
//    if (!order.getUser().getId().equals(user.getId())) {
//      throw new IllegalStateException("주문을 취소할 수 없습니다.");
//    }
//
//    orderProduct.cancel();
//  }
//
//  @Transactional
//  public void returnOrderProduct(Long orderProductId, User user) {
//    OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
//        .orElseThrow(() -> new IllegalArgumentException("주문 상품이 존재하지 않습니다."));
//
//    Order order = orderProduct.getOrder();
//    if (!order.getUser().getId().equals(user.getId())) {
//      throw new IllegalStateException("반품할 수 없습니다.");
//    }
//
//    orderProduct.requestReturn();  // 반품 처리
//  }
//
//  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
//  @Transactional
//  public void completeReturn() {
//    List<OrderProduct> returnRequestedProducts = orderProductRepository.findAllByStatus(
//        OrderProductStatus.RETURN_REQUESTED);
//
//    for (OrderProduct orderProduct : returnRequestedProducts) {
//      orderProduct.completeReturn();
//    }
//  }

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
  @Transactional
  public void updateOrderStatus() {
    List<OrderProduct> orderedProductList = orderProductRepository.findAllByStatus(
        OrderProductStatus.ORDERED);

    for (OrderProduct orderProduct : orderedProductList) {
      orderProduct.orderedToShipped();
    }

    List<OrderProduct> shippedProductList = orderProductRepository.findAllByStatus(
        OrderProductStatus.SHIPPED);

    for (OrderProduct orderProduct : shippedProductList) {
      orderProduct.shippedToDelivered();
    }
  }
}
