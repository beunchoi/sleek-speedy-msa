package com.hanghae.paymentservice.domain.payment.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderResponseDto {
  private String productId;
  private Integer quantity;
  private Integer price;
  private Integer totalPrice;
  private String userId;
  private LocalDateTime createdAt;
  private String orderId;

  public OrderResponseDto(String productId, String orderId, Integer quantity) {
    this.productId = productId;
    this.orderId = orderId;
    this.quantity = quantity;
  }
}
