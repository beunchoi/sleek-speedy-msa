package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.common.dto.ProductResponseDto;
import com.hanghae.userservice.domain.user.client.wish.ProductServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductFetchService {

  private final ProductServiceClient productServiceClient;

  @CircuitBreaker(name = "ProductFetchService", fallbackMethod = "fallbackGetProductByProductId")
  @Retry(name = "ProductFetchServiceRetry", fallbackMethod = "fallbackGetProductByProductId")
  public ProductResponseDto getProductByProductId(String productId) {
    return productServiceClient.getProductByProductId(productId);
  }

  public ProductResponseDto fallbackGetProductByProductId(String productId, Throwable t) {
    log.error("서킷브레이커 오픈, productId={}, error={}", productId, t.getMessage());
    return new ProductResponseDto(productId);
  }

}
