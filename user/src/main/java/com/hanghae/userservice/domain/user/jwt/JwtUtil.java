package com.hanghae.userservice.domain.user.jwt;

import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

  public static final String BEAR_PREFIX = "Bearer ";
  public static final String AUTHORIZATION_KEY = "auth";
  private final long ACCESS_TOKEN_TIME = 30 * 60 * 1000L;
  public static final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L;

  @Value("${jwt.secret.key}")
  private String secretKey;
  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  public String createAccessToken(String userId, UserRoleEnum role) {
    Date date = new Date();

    return BEAR_PREFIX +
        Jwts.builder()
          .setSubject(userId)
          .claim(AUTHORIZATION_KEY, role) // 사용자 권한
          .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME)) // 만료 시간
          .setIssuedAt(date) // 발급일
          .signWith(key, signatureAlgorithm) // 암호화 알고리즘
          .compact();
  }

  public String createRefreshToken(String userId, UserRoleEnum role) {
    Date date = new Date();

    return Jwts.builder()
            .setSubject(userId)
            .claim(AUTHORIZATION_KEY, role)
            .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token, 만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
    } catch (IllegalArgumentException e) {
      log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
    }
    return false;
  }

  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

}
