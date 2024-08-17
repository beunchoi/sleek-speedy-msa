package com.hanghae.sleekspeedy.domain.basket.dto;

import com.hanghae.sleekspeedy.domain.basket.entity.Basket;
import lombok.Getter;

@Getter
public class BasketProductResponse {

  private Basket basket;
  private Long productId;
  private int count;

//  public BasketProductResponse(BasketProduct basketProduct) {
//    this.basket = basketProduct.getBasket();
//    this.product = basketProduct.getProduct();
//    this.count = basketProduct.getCount();
//  }
}
