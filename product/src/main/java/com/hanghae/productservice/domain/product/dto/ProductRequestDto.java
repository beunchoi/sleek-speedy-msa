package com.hanghae.productservice.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

  private String title;
  private Integer price;
  private String category;
  private String description;
  private Integer stock;
}
