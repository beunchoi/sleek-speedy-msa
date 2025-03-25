package com.hanghae.productservice.domain.product.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StockCheckEvent {
  private String productId;
  private Integer price;
  private String userId;
  private String orderId;
  private Integer quantity;
  private String paymentMethodId;
}
