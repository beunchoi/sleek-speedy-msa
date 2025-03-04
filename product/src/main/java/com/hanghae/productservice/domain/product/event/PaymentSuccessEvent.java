package com.hanghae.productservice.domain.product.event;

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
