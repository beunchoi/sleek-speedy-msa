package com.hanghae.orderservice.domain.order.dto;

import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ReturnResponseDto {
  private String userId;
  private String orderId;
  private String productId;
  private Integer quantity;
  private Integer price;
  private Integer totalPrice;
  private OrderStatus status;
  private LocalDate returnRequestedDate;

  public ReturnResponseDto(Order order) {
    this.userId = order.getUserId();
    this.orderId = order.getOrderId();
    this.productId = order.getProductId();
    this.quantity = order.getQuantity();
    this.price = order.getPrice();
    this.status = order.getStatus();
    this.returnRequestedDate = order.getReturnRequestedDate();
  }
}
