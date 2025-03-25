package com.hanghae.orderservice.domain.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderCreatedEvent {
  private String orderId;
  private String paymentMethodId;
  private Integer totalPrice;
  private String productId;
  private Integer quantity;

}
