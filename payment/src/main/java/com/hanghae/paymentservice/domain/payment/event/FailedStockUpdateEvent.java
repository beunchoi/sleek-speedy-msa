package com.hanghae.paymentservice.domain.payment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FailedStockUpdateEvent {
  private String productId;
  private String orderId;
  private Integer quantity;
}
