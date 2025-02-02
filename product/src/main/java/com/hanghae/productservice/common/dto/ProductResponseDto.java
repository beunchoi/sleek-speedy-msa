package com.hanghae.productservice.common.dto;

import lombok.Data;

@Data
public class ProductResponseDto {

  private String productId;
  private String title;
  private Integer price;
  private String category;
  private String description;
  private Integer stock;
}
