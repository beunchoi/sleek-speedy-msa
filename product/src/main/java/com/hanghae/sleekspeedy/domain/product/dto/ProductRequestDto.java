package com.hanghae.sleekspeedy.domain.product.dto;

import lombok.Getter;

@Getter
public class ProductRequestDto {

  private String title;
  private String image;
  private int price;
  private String category;
  private String description;
}
