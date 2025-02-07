package com.hanghae.paymentservice.domain.payment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentFailedEvent {
  private String productId;
  private String orderId;
  private Integer quantity;

  public PaymentFailedEvent(String orderId) {
    this.orderId = orderId;
  }
}
