package com.hanghae.orderservice.domain.order.event;

import lombok.Data;

@Data
public class SavePaymentEvent {
  private String tid;
  private String orderId;
  private String userId;
  private String productId;
  private String quantity;

  public SavePaymentEvent(String tid, String orderId, String userId,
      String productId, String quantity) {
    this.tid = tid;
    this.orderId = orderId;
    this.userId = userId;
    this.productId = productId;
    this.quantity = quantity;
  }
}
