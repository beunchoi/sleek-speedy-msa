package com.hanghae.orderservice.domain.order.service;

import com.hanghae.orderservice.common.dto.ProductResponseDto;
import com.hanghae.orderservice.domain.order.client.ProductServiceClient;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductFetchService {

  private final ProductServiceClient productServiceClient;

  @Retry(name = "getProductByProductId", fallbackMethod = "fallbackGetProductByProductId")
  public ProductResponseDto getProductByProductId(String productId) {
    log.info("FeignClient 호출, productId={}", productId);
    return productServiceClient.getProductByProductId(productId);
  }

  @Retry(name = "increaseProductStock", fallbackMethod = "fallbackIncreaseProductStock")
  public void increaseProductStock(String productId, int quantity) {
    log.info("FeignClient 호출, productId={}, quantity={}", productId, quantity);
    productServiceClient.increaseProductStock(productId, quantity);
  }

  public ProductResponseDto fallbackGetProductByProductId(String productId, Throwable t) {
    log.error("fallbackGetProductByProductId 메서드 실행, productId={}, error={}",
        productId, t.getMessage());
    return new ProductResponseDto(productId);
  }

  public void fallbackIncreaseProductStock(String productId, int quantity, Throwable t) {
    log.error("fallbackIncreaseProductStock 메서드 실행, productId={}, quantity={}, error={}",
        productId, quantity, t.getMessage());
    throw new RuntimeException(t.getMessage());
  }

}
