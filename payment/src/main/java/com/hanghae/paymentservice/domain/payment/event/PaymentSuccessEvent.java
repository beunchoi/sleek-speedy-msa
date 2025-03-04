package com.hanghae.paymentservice.domain.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PaymentSuccessEvent {
  private String productId;
  private String orderId;
  private Integer quantity;
}
