package com.hanghae.orderservice.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderRequestDto {
  private Integer quantity;
  private String paymentMethodId;
}
