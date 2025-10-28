package com.hanghae.userservice.domain.user.service;

import com.hanghae.common.exception.user.InvalidTokenException;
import com.hanghae.common.exception.user.TokenMismatchException;
import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import com.hanghae.userservice.domain.user.jwt.JwtUtil;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final RedisTemplate<String, String> redisTemplate;
  private final JwtUtil jwtUtil;
  public static final String REFRESH_TOKEN_KEY = "refreshToken:";

  public String reissueToken(String refreshToken) {

    if (!jwtUtil.validateToken(refreshToken)) {
      throw new InvalidTokenException("토큰이 유효하지 않습니다.");
    }

    String userId = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();
    if (!validateTokenUsingRedis(userId, refreshToken)) {
      throw new TokenMismatchException("토큰이 불일치합니다.");
    }

    String roleString = jwtUtil.getUserInfoFromToken(refreshToken)
        .get(JwtUtil.AUTHORIZATION_KEY).toString();
    UserRoleEnum role = UserRoleEnum.valueOf(roleString);

    return jwtUtil.createAccessToken(userId, role);
  }

  public void saveRefreshToken(String userId, String refreshToken) {
    redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY + userId,
        refreshToken, JwtUtil.REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
  }

  public boolean validateTokenUsingRedis(String userId, String refreshTokenFromClient) {
    String refreshTokenFromRedis = getRefreshToken(userId);
    return refreshTokenFromRedis != null && refreshTokenFromRedis.equals(refreshTokenFromClient);
  }

  public String getRefreshToken(String userId) {
    return redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY + userId);
  }

}
