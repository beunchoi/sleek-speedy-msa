package com.hanghae.sleekspeedy.domain.order.dto;

import com.hanghae.sleekspeedy.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class OrderResponseDto {
  private String productId;
  private Integer quantity;
  private Integer price;
  private Integer totalPrice;

  private String orderId;
  private String userId;

  public OrderResponseDto(Order order) {
    this.productId = order.getProductId();
    this.quantity = order.getQuantity();
    this.price = order.getPrice();
    this.totalPrice = order.getTotalPrice();
    this.orderId = order.getOrderId();
    this.userId = order.getUserId();
  }
}
