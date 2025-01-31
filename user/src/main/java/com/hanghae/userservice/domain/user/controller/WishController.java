package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.common.dto.ResponseMessage;
import com.hanghae.userservice.common.util.ParseRequestUtil;
import com.hanghae.userservice.domain.user.entity.Wish;
import com.hanghae.userservice.domain.user.service.WishService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {

  private final WishService wishService;

  @PostMapping("/{productId}")
  public ResponseEntity<ResponseMessage> createUpdateWish(HttpServletRequest request, @PathVariable("productId") String productId) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    Wish wish = wishService.createUpdateWish(userId, productId);

    String resultMessage;
    if (wish.isActive()) {
      resultMessage = "관심 상품으로 등록되었습니다.";
    } else {
      resultMessage = "관심 상품에서 제외되었습니다.";
    }

    ResponseMessage message = ResponseMessage.builder()
        .data(wish)
        .statusCode(200)
        .resultMessage(resultMessage)
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @GetMapping
  public ResponseEntity<ResponseMessage> getMyWishList(HttpServletRequest request) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    List<Wish> wishList = wishService.getMyWishList(userId);

    ResponseMessage message = ResponseMessage.builder()
        .data(wishList)
        .statusCode(200)
        .resultMessage("관심 상품 목록 조회")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

}

