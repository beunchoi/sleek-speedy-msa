//package com.hanghae.orderservice.global.jwt;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.UnsupportedJwtException;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.http.HttpServletRequest;
//import java.security.Key;
//import java.util.Base64;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//@Slf4j(topic = "JwtUtil")
//@Component
//public class JwtUtil {
//  // Header KEY 값
//  public static final String AUTHORIZATION_HEADER = "Authorization";
//  // 사용자 권한 값의 KEY
//  public static final String AUTHORIZATION_KEY = "auth";
//  // Token 식별자
//  public static final String BEARER_PREFIX = "Bearer ";
//  // 토큰 만료시간
//  private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
//
//  @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
//  private String secretKey;
//  private Key key;
//  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//  @PostConstruct
//  public void init() {
//    byte[] bytes = Base64.getDecoder().decode(secretKey);
//    key = Keys.hmacShaKeyFor(bytes);
//  }
//
//  public Long getUserIdFromToken(String token) {
//    return Jwts.parserBuilder()
//        .setSigningKey(key)
//        .build()
//        .parseClaimsJws(token.replace(BEARER_PREFIX, ""))
//        .getBody()
//        .get("userId", Long.class);
//  }
//
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
//}
