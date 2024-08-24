package com.hanghae.userservice.domain.basket.controller;

import com.hanghae.userservice.domain.basket.service.BasketService;
import com.hanghae.userservice.global.response.ResponseDto;
import com.hanghae.userservice.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BasketController {

  private final BasketService basketService;
  @PostMapping("/basketProducts/{productId}")
  public ResponseEntity<ResponseDto> addProductToBasket(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
    basketService.addProductToBasket(userDetails.getUser(), productId);
    return ResponseEntity.ok().body(ResponseDto.success(200));
  }

//  @GetMapping("/myBasketProducts")
//  public List<BasketProductResponse> getMyBasketProducts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//    return basketService.getMyBasketProducts(userDetails.getUser());
//  }
}