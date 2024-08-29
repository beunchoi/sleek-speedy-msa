package com.hanghae.userservice.domain.wish.dto;

import lombok.Data;

@Data
public class ProductResponseDto {

  private String productId;
  private String title;
  private String image;
  private Integer price;
  private String category;
  private String description;
  private Integer stock;
}
