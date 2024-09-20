package com.hanghae.paymentservice.domain.payment.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {
  private String tid;
  private String orderId;
  private String userId;
  private String productId;
  private String quantity;
  private String pgToken;

  public PaymentResponseDto(String tid, String orderId, String userId, String productId,
      String quantity, String pgToken) {
    this.tid = tid;
    this.orderId = orderId;
    this.userId = userId;
    this.productId = productId;
    this.quantity = quantity;
    this.pgToken = pgToken;
  }
}
