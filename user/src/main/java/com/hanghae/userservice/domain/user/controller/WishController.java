package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.domain.user.dto.wish.WishResponseDto;
import com.hanghae.userservice.domain.user.service.WishService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class WishController {

  private final WishService wishService;
  @PostMapping("/wish/{productId}")
  public ResponseEntity<WishResponseDto> createUpdateWish(@RequestHeader("X-User-Id") String userId, @PathVariable String productId) {
    WishResponseDto response = wishService.createUpdateWish(userId, productId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/wish")
  public ResponseEntity<List<WishResponseDto>> getMyWishList(@RequestHeader("X-User-Id") String userId) {
    List<WishResponseDto> wishList = wishService.getMyWishList(userId);
    return ResponseEntity.status(HttpStatus.OK).body(wishList);
  }

  @PutMapping("/wish/{productId}")
  public ResponseEntity<WishResponseDto> deleteWish(@RequestHeader("X-User-Id") String userId, @PathVariable String productId) {
    WishResponseDto response = wishService.deleteWish(userId, productId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}

