package com.hanghae.productservice.domain.product.repository;

import com.hanghae.productservice.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductStockRepository {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String STOCK_KEY = "product:stock:";

  public void initializeStock(String productId, Product product) {
    redisTemplate.opsForValue().set(STOCK_KEY + productId,
        String.valueOf(product.getStock()));
  }

  public String getProductStock(String productId) {
    return redisTemplate.opsForValue().get(STOCK_KEY + productId);
  }

  public void increaseStock(String productId, int quantity) {
    redisTemplate.opsForValue().increment(STOCK_KEY + productId, quantity);
  }

}
