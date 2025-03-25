package com.hanghae.productservice.domain.product.repository;

import com.hanghae.productservice.domain.product.entity.Product;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductStockRepository {

  private final RedisTemplate<String, String> redisTemplate;
  private final ProductRepository productRepository;

  private static final String STOCK_KEY_PREFIX = "product:stock:";
  private static final String PRICE_KEY_PREFIX = "product:price:";

  public Long checkAndDecreaseStock(String productId, int quantity) {
    String stockKey = STOCK_KEY_PREFIX + productId;

    // Lua 스크립트를 사용하여 재고를 원자적으로 감소
    String luaScript =
        "local stock = redis.call('GET', KEYS[1]) " +
            "if not stock then " +  // 키가 존재하지 않으면
            "return -2 " +
            "end " +
            "if tonumber(stock) < tonumber(ARGV[1]) then " +
            "return -1 " + // 재고 부족
            "else " +
            "redis.call('DECRBY', KEYS[1], ARGV[1]) " +
            "return redis.call('GET', KEYS[1]) " +
            "end";

    List<String> keys = Collections.singletonList(stockKey);

    return redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), keys,
        String.valueOf(quantity));
  }

  public String getPrice(String productId) {
    String price = redisTemplate.opsForValue().get(PRICE_KEY_PREFIX + productId);

    if (price == null) {
      Product product = productRepository.findByProductId(productId)
          .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
      price = String.valueOf(product.getPrice());

      redisTemplate.opsForValue().set(PRICE_KEY_PREFIX + productId, price);
    }

    return price;
  }

  public void initializeStock(String productId, Product product) {
    redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + productId,
        String.valueOf(product.getStock()));
  }

  public String getProductStock(String productId) {
    return redisTemplate.opsForValue().get(STOCK_KEY_PREFIX + productId);
  }

  public void increaseStock(String productId, int quantity) {
    redisTemplate.opsForValue().increment(STOCK_KEY_PREFIX + productId, quantity);
  }

}
