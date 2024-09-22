package com.hanghae.productservice.domain.product.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FailedStockUpdateEvent {
  private String productId;
  private String orderId;
  private Integer quantity;
}
