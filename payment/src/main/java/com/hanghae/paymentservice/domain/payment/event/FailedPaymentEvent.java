package com.hanghae.paymentservice.domain.payment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FailedPaymentEvent {
  private String productId;
  private String orderId;
  private Integer quantity;

  public FailedPaymentEvent(String orderId) {
    this.orderId = orderId;
  }
}
