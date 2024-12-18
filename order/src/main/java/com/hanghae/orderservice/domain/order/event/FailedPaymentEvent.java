package com.hanghae.orderservice.domain.order.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FailedPaymentEvent {
  private String productId;
  private String orderId;
  private Integer quantity;
}
