package com.hanghae.sleekspeedy.domain.user.dto;

import java.util.Date;
import lombok.Data;

@Data
public class OrderResponse {
  private String productId;
  private Integer quantity;
  private Integer price;
  private Integer totalPrice;
  private Date createdAt;

  private String orderId;
}
