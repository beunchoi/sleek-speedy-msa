package com.hanghae.orderservice.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponseDto {

  private String productId;
  private String title;
  private Integer price;
  private String category;
  private String description;
  private Integer stock;

  public ProductResponseDto(String productId) {
    this.productId = productId;
  }

}
