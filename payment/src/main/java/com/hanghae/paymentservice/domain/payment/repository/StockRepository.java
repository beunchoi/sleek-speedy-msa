package com.hanghae.paymentservice.domain.payment.repository;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockRepository {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String STOCK_KEY_PREFIX = "product:stock:";

  public void getAndDecreaseStock(String productId, int quantity) {
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
    Long result = redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), keys,
        String.valueOf(quantity));

    if (result == null || result == -2) {
      throw new IllegalStateException("재고 조회 중 오류가 발생했습니다.");
    } else if (result == -1) {
      throw new IllegalStateException("재고가 부족합니다.");
    } else {

    }
  }

}
