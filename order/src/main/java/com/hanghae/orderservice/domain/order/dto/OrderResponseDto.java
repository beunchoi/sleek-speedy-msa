package com.hanghae.orderservice.domain.order.dto;

import com.hanghae.orderservice.domain.order.entity.Order;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderResponseDto {
  private String productId;
  private Integer quantity;
  private Integer price;
  private String userId;
  private LocalDateTime createdAt;
  private String orderId;

  public OrderResponseDto(Order order) {
    this.productId = order.getProductId();
    this.quantity = order.getQuantity();
    this.price = order.getPrice();
    this.createdAt = order.getCreatedAt();
    this.orderId = order.getOrderId();
    this.userId = order.getUserId();
  }
}
