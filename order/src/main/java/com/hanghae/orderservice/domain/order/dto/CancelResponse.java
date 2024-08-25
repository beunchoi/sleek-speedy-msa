package com.hanghae.orderservice.domain.order.dto;

import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import lombok.Data;

@Data
public class CancelResponse {

  private String userId;
  private String orderId;
  private String productId;
  private Integer quantity;
  private Integer price;
  private Integer totalPrice;
  private OrderStatus status;

  public CancelResponse(Order order) {
    this.userId = order.getUserId();
    this.orderId = order.getOrderId();
    this.productId = order.getProductId();
    this.quantity = order.getQuantity();
    this.price = order.getPrice();
    this.totalPrice = order.getTotalPrice();
    this.status = order.getStatus();
  }
}
