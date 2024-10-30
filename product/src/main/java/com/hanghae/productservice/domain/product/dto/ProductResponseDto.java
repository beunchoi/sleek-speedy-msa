package com.hanghae.productservice.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hanghae.productservice.domain.product.entity.Product;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDto {

  private String productId;
  private String title;
  private String image;
  private Integer price;
  private String category;
  private String description;
  private Integer stock;

  public ProductResponseDto(Product product) {
    this.productId = product.getProductId();
    this.title = product.getTitle();
    this.image = product.getImage();
    this.price = product.getPrice();
    this.category = product.getCategory();
    this.description = product.getDescription();
    this.stock = product.getStock();
  }
}
