package com.hanghae.productservice.domain.product.entity;

import com.hanghae.productservice.common.util.Timestamp;
import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor
public class Product extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String productId = UUID.randomUUID().toString();
  @Column(nullable = false)
  private String title;
  @Column(nullable = false)
  private Integer price;
  @Column(nullable = false)
  private String category;
  @Column(nullable = false)
  private String description;
  @Column(nullable = false)
  private Integer stock;

  public Product(ProductRequestDto requestDto) {
    this.title = requestDto.getTitle();
    this.price = requestDto.getPrice();
    this.category = requestDto.getCategory();
    this.description = requestDto.getDescription();
    this.stock = requestDto.getStock();
  }

  public void increaseStock(int quantity) {
    this.stock += quantity;
  }

  public void updateStock(int stock) {
    this.stock = stock;
  }

}