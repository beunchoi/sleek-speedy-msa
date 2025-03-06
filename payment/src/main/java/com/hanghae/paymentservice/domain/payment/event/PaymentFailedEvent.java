package com.hanghae.paymentservice.domain.payment.event;

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

  public PaymentFailedEvent(String orderId) {
    this.orderId = orderId;
  }
}
