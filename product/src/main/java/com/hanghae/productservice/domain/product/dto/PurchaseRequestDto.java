package com.hanghae.productservice.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDto {
  private Integer quantity;
  private String paymentMethodId;
}
