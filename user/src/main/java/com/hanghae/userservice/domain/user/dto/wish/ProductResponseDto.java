package com.hanghae.userservice.domain.user.dto.wish;

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
