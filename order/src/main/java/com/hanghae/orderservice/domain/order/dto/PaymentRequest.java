package com.hanghae.orderservice.domain.order.dto;

import lombok.Data;

@Data
public class PaymentRequest {
  private String orderId;
  private String userId;
  private String productId;
  private Integer quantity;
  private Integer totalPrice;

  public PaymentRequest(OrderResponseDto response, String userId) {
    this.orderId = response.getOrderId();
    this.userId = userId;
    this.productId = response.getProductId();
    this.quantity = response.getQuantity();
    this.totalPrice = response.getTotalPrice();
  }
}
