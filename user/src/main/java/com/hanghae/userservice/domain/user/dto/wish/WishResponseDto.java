package com.hanghae.userservice.domain.user.dto.wish;

import com.hanghae.userservice.domain.user.entity.Wish;
import lombok.Data;

@Data
public class WishResponseDto {
  private String userId;
  private String productId;
  private boolean active;

  public WishResponseDto(Wish wish) {
    this.userId = wish.getUserId();
    this.productId = wish.getProductId();
    this.active = wish.isActive();
  }
}
