package com.hanghae.userservice.domain.user.controller;

import com.hanghae.common.dto.ResponseMessage;
import com.hanghae.userservice.domain.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refresh-token")
public class TokenController {

  private final TokenService tokenService;

  @PostMapping
  public ResponseEntity<?> reissueToken(@CookieValue(value = "Refresh-token", required = false)
  String refreshToken) {

    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 존재하지 않습니다.");
    }

    String newAccessToken = tokenService.reissueToken(refreshToken);

    ResponseMessage message = ResponseMessage.builder()
        .statusCode(200)
        .resultMessage("액세스 토큰이 재발급되었습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.AUTHORIZATION, newAccessToken)
        .body(message);
  }

}
