package com.hanghae.orderservice.domain.order.event;

import lombok.Getter;

@Getter
public class StockCheckEvent {
  private String productId;
  private Integer price;
  private String userId;
  private String orderId;
  private Integer quantity;
  private String paymentMethodId;
}
