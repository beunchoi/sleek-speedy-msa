package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import com.hanghae.userservice.domain.user.service.TokenService;
import com.hanghae.userservice.domain.user.service.UserService;
import com.hanghae.userservice.global.jwt.JwtUtil;
import com.hanghae.userservice.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class TokenController {
  private final JwtUtil jwtUtil;
  private final TokenService tokenService;
  private final UserService userService;

  @PostMapping("/token")
  public ResponseEntity<?> reissueToken(@CookieValue(value = "Refresh-token", required = false)
  String refreshToken) {

    // 리프레시 토큰 누락 여부 확인
    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 누락되었습니다.");
    }

    // 토큰 유효성 검증
    if (!jwtUtil.validateToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않거나 만료된 토큰입니다.");
    }

    String userId = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();

    // 저장된 리프레시 토큰과 상호 검증
    if (!tokenService.validateRefreshToken(userId, refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 정보가 일치하지 않습니다.");
    }

    // 새로운 액세스 토큰 발급
    UserRoleEnum role = userService.getUserRole(userId);
    String newAccessToken = JwtAuthenticationFilter
        .BEARER_PREFIX + jwtUtil.createAccessToken(userId, role);

    return ResponseEntity.status(HttpStatus.OK)
        .header("Authorization", newAccessToken).body("새로운 액세스 토큰이 발급되었습니다.");
  }
}
