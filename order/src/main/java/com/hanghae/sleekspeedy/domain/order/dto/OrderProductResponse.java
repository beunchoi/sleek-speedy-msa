package com.hanghae.sleekspeedy.domain.order.dto;

import com.hanghae.sleekspeedy.domain.order.entity.Order;
import com.hanghae.sleekspeedy.domain.order.entity.OrderProduct;
import com.hanghae.sleekspeedy.domain.order.entity.OrderProductStatus;
import lombok.Getter;

@Getter
public class OrderProductResponse {

//  private Product product;
  private Order order;
  private OrderProductStatus status;
  private int count;

//  public OrderProductResponse(OrderProduct orderProduct) {
//    this.product = orderProduct.getProduct();
//    this.order = orderProduct.getOrder();
//    this.status = orderProduct.getStatus();
//    this.count = orderProduct.getCount();
//  }
}
