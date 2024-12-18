package com.hanghae.apigateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

  @Value("${token.secret}")
  private String secretKey;
  private Key key;

  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  public AuthorizationHeaderFilter() {
    super(Config.class);
  }

  public static class Config {

  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
      }

      String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
      String jwt = authorizationHeader.replace("Bearer", "");

      if (!isJwtValid(jwt)) {
        return onError(exchange, "유효한 JWT 토큰이 아닙니다.", HttpStatus.UNAUTHORIZED);
      }

      return chain.filter(exchange);
    };
  }

  private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(httpStatus);
    log.error(err);

    byte[] bytes = "The requested token is invalid.".getBytes(StandardCharsets.UTF_8);
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
    return response.writeWith(Flux.just(buffer));
  }

  private boolean isJwtValid(String jwt) {
    boolean returnValue = true;

    String subject = null;

    try {
      subject = Jwts
          .parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(jwt).getBody()
          .getSubject();
    } catch (Exception ex) {
      returnValue = false;
    }

    if (subject == null || subject.isEmpty()) {
      returnValue = false;
    }

    return returnValue;
  }
}
