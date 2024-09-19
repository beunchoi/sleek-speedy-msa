package com.hanghae.userservice.domain.user.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
  private final RedisTemplate<String, String> redisTemplate;
  private final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60;

  public void saveRefreshToken(String userId, String refreshToken) {
    redisTemplate.opsForValue().set("refreshToken: " + userId,
        refreshToken, REFRESH_TOKEN_EXPIRATION, TimeUnit.SECONDS);
  }

  public String getRefreshToken(String userId) {
    return redisTemplate.opsForValue().get("refreshToken: " + userId);
  }

  public boolean validateRefreshToken(String userId, String refreshTokenFromClient) {
    String refreshTokenFromRedis = getRefreshToken(userId);
    return refreshTokenFromRedis != null && refreshTokenFromRedis.equals(refreshTokenFromClient);
  }
}
