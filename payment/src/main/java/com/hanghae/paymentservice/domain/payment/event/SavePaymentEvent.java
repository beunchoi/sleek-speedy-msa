package com.hanghae.paymentservice.domain.payment.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SavePaymentEvent {
  private String tid;
  private String orderId;
  private String userId;
  private String productId;
  private String quantity;
}
