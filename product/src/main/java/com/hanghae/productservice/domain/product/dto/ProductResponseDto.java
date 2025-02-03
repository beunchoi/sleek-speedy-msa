package com.hanghae.productservice.domain.product.dto;

import com.hanghae.productservice.domain.product.entity.Product;
import lombok.Data;

@Data
public class ProductResponseDto {

  private String productId;
  private String title;
  private Integer price;
  private String category;
  private String description;
  private Integer stock;

  public ProductResponseDto(Product product) {
    this.productId = product.getProductId();
    this.title = product.getTitle();
    this.price = product.getPrice();
    this.category = product.getCategory();
    this.description = product.getDescription();
    this.stock = product.getStock();
  }
}
