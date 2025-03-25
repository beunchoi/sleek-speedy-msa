package com.hanghae.orderservice.domain.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderFailedEvent {
  private String productId;
  private Integer quantity;
}
