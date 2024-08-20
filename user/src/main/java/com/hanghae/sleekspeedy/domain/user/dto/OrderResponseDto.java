package com.hanghae.sleekspeedy.domain.user.dto;

import lombok.Data;

@Data
public class OrderResponseDto {
  private String productId;
  private Integer quantity;
  private Integer price;
  private Integer totalPrice;
  private String orderId;
}
