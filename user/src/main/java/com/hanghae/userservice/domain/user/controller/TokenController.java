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
  public ResponseEntity<?> reissueToken(@CookieValue("Refresh-token") String refreshToken) {
    String userId = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();

    if (!jwtUtil.validateToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않거나 만료된 토큰입니다.");
    }

    if (!tokenService.validateRefreshToken(userId, refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 정보가 일치하지 않습니다.");
    }

    UserRoleEnum role = userService.getUserRole(userId);
    String newAccessToken = JwtAuthenticationFilter.BEARER_PREFIX + jwtUtil.createAccessToken(userId, role);

    return ResponseEntity.status(HttpStatus.OK).header("Authorization", newAccessToken).build();
  }
}
