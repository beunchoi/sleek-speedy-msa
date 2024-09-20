package com.hanghae.paymentservice.domain.payment.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReturnRequestResponse {
  private String userId;
  private String orderId;
  private String productId;
  private Integer quantity;
  private Integer price;
  private Integer totalPrice;
  private OrderStatus status;
  private LocalDate returnRequestedDate;

  public ReturnRequestResponse(Order order) {
    this.userId = order.getUserId();
    this.orderId = order.getOrderId();
    this.productId = order.getProductId();
    this.quantity = order.getQuantity();
    this.price = order.getPrice();
    this.totalPrice = order.getTotalPrice();
    this.status = order.getStatus();
    this.returnRequestedDate = order.getReturnRequestedDate();
  }
}
