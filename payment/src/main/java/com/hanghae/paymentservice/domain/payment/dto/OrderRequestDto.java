package com.hanghae.paymentservice.domain.payment.dto;

import lombok.Getter;

@Getter
public class OrderRequestDto {
  private String productId;
  private Integer quantity;
  private Integer price;
}
