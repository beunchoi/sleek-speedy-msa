package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.common.dto.ProductResponseDto;
import com.hanghae.userservice.domain.user.client.wish.ProductServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductFetchService {

  private final ProductServiceClient productServiceClient;

  @CircuitBreaker(name = "ProductFetchService", fallbackMethod = "fallbackGetProductByProductId")
  public ProductResponseDto getProductByProductId(String productId) {
    log.info("FeignClient 호출, productId={}", productId);
    return productServiceClient.getProductByProductId(productId);
  }

  public ProductResponseDto fallbackGetProductByProductId(String productId, Throwable t) {
    log.error("fallback 메서드 실행, productId={}, error={}", productId, t.getMessage());
    return new ProductResponseDto(productId);
  }

}
