package com.hanghae.productservice.domain.product.dto;

import lombok.Getter;

@Getter
public class PurchaseRequestDto {
  private Integer quantity;
  private String paymentMethodId;
}
