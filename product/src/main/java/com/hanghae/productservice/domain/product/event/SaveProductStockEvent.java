package com.hanghae.productservice.domain.product.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaveProductStockEvent {
  private String productId;
  private String orderId;
  private Integer quantity;
}
