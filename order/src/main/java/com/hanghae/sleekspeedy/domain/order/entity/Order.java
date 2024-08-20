package com.hanghae.sleekspeedy.domain.order.entity;

import com.hanghae.sleekspeedy.domain.order.dto.OrderRequestDto;
import com.hanghae.sleekspeedy.global.util.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor
public class Order extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String userId;
  @Column(nullable = false, unique = true)
  private String orderId;

  @Column(nullable = false)
  private String productId;
  @Column(nullable = false)
  private Integer quantity;
  @Column(nullable = false)
  private Integer price;
  @Column(nullable = false)
  private Integer totalPrice;

  public Order(OrderRequestDto request, String orderId, Integer totalPrice, String userId) {
    this.userId = userId;
    this.orderId = orderId;
    this.productId = request.getProductId();
    this.quantity = request.getQuantity();
    this.price = request.getPrice();
    this.totalPrice = totalPrice;
  }
}
