package com.hanghae.productservice.domain.product.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderFailedEvent {
  private String productId;
  private Integer quantity;
}
