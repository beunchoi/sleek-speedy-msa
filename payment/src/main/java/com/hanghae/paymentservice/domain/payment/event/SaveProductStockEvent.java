package com.hanghae.paymentservice.domain.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaveProductStockEvent {
  private String productId;
  private String orderId;
  private Integer quantity;
}
