package com.hanghae.userservice.domain.wish.controller;

import com.hanghae.userservice.domain.wish.dto.WishResponseDto;
import com.hanghae.userservice.domain.wish.service.WishService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class WishController {

  private final WishService wishService;
  @PostMapping("/wishs/{userId}/{productId}")
  public ResponseEntity createUpdateWish(@PathVariable String userId, @PathVariable String productId) {
    wishService.createUpdateWish(userId, productId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/wishs/{userId}")
  public ResponseEntity<List<WishResponseDto>> getMyWishList(String userId) {
    List<WishResponseDto> wishList = wishService.getMyWishList(userId);
    return ResponseEntity.status(HttpStatus.OK).body(wishList);
  }

  @PutMapping("/wishs/{userId}/{productId}")
  public ResponseEntity deleteWish(@PathVariable String userId, @PathVariable String productId) {
    wishService.deleteWish(userId, productId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}

