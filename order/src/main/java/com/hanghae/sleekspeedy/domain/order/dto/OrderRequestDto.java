package com.hanghae.sleekspeedy.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderRequestDto {
  private String productId;
  private Integer quantity;
  private Integer price;
}
