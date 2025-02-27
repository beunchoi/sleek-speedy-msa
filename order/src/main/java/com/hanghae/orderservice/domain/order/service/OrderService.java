package com.hanghae.orderservice.domain.order.service;

import com.hanghae.orderservice.common.dto.ProductResponseDto;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.dto.ReturnResponseDto;
import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.event.PaymentFailedEvent;
import java.util.List;

public interface OrderService {

  OrderResponseDto createOrder(OrderRequestDto requestDto, String productId, String userId);
  ProductResponseDto checkProduct(String productId);
  List<OrderResponseDto> getOrdersByUserId(String userId);
  OrderResponseDto cancelOrder(String userId, String orderId);
  ReturnResponseDto requestProductReturn(String userId, String orderId);
  void rollbackOrder(PaymentFailedEvent event);
  Order validateAndGetOrder(String userId, String orderId);
  Order findOrderByOrderId(String orderId);

}
