package com.hanghae.productservice.domain.product.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCacheRepository {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String PRODUCT_CACHE_KEY = "getProducts::product:page:1:size:20";

  public void deleteCachePage() {
    redisTemplate.delete(PRODUCT_CACHE_KEY);
  }

}
