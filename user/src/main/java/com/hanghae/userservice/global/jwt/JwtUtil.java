package com.hanghae.userservice.global.jwt;

import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {
  // Header KEY 값
  public static final String AUTHORIZATION_HEADER = "Authorization";
  // 사용자 권한 값의 KEY
  public static final String AUTHORIZATION_KEY = "auth";
  // Token 식별자
  public static final String BEARER_PREFIX = "Bearer ";
  // 토큰 만료시간
  private final long TOKEN_TIME = 24 * 60 * 60 * 1000L;

  @Value("${token.secret}") // Base64 Encode 한 SecretKey
  private String secretKey;
  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  // 토큰 생성
  public String createToken(String userId, UserRoleEnum role) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(userId)
            .claim(AUTHORIZATION_KEY, role) // 사용자 권한
            .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm) // 암호화 알고리즘
            .compact();
  }

//  // header 에서 JWT 가져오기
//  public String getJwtFromHeader(HttpServletRequest request) {
//    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//      return bearerToken.substring(7);
//    }
//    return null;
//  }
//
//  // 토큰 검증
//  public boolean validateToken(String token) {
//    try {
//      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//      return true;
//    } catch (SecurityException | MalformedJwtException e) {
//      log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
//    } catch (ExpiredJwtException e) {
//      log.error("Expired JWT token, 만료된 JWT token 입니다.");
//    } catch (UnsupportedJwtException e) {
//      log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
//    } catch (IllegalArgumentException e) {
//      log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
//    }
//    return false;
//  }
//
//  // 토큰에서 사용자 정보 가져오기
//  public Claims getUserInfoFromToken(String token) {
//    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
//  }
}