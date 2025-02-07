package com.hanghae.paymentservice.domain.payment.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCreatedEvent {
  private String orderId;
  private String paymentMethodId;
  private Integer totalPrice;
  private String productId;
  private Integer quantity;

}
