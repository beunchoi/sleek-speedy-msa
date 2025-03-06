package com.hanghae.orderservice.domain.order.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class PaymentFailedEvent {
  private String productId;
  private String orderId;
  private Integer quantity;
}
