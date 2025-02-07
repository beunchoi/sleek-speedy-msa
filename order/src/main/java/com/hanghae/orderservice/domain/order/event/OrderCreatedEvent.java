package com.hanghae.orderservice.domain.order.event;

import lombok.Data;

@Data
public class OrderCreatedEvent {
  private String orderId;
  private String paymentMethodId;
  private Integer totalPrice;
  private String productId;
  private Integer quantity;

  public OrderCreatedEvent(String orderId, String paymentMethodId, int totalPrice,
      String productId, int quantity) {
    this.orderId = orderId;
    this.paymentMethodId = paymentMethodId;
    this.totalPrice = totalPrice;
    this.productId = productId;
    this.quantity = quantity;
  }

}
