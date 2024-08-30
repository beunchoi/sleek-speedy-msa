package com.hanghae.orderservice.domain.order.service;

import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
  private final OrderRepository orderRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final RedissonClient redissonClient;
  private final RestTemplate restTemplate = new RestTemplate();
  private static final String STOCK_KEY_PREFIX = "product:stock:";
  private static final String HOST = "https://open-api.kakaopay.com";

  @Transactional
  public void approvePayment(String pgToken) {
    String lockKey = "payment:lock:" + pgToken;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      lock.lock();

      String tid = redisTemplate.opsForValue().get("tid");
      String orderId = redisTemplate.opsForValue().get("orderId");
      String userId = redisTemplate.opsForValue().get("userId");
      String productId = redisTemplate.opsForValue().get("productId");
      String quantity = redisTemplate.opsForValue().get("quantity");

      this.paymentSuccess(productId, orderId, Integer.valueOf(quantity));

      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "SECRET_KEY " + "DEVCFD942EFA3112904ED6632AC7F492D0E4BC34"); // 실제 Secret Key를 사용해야 합니다.
      headers.add("Content-Type", "application/json");

      Map<String, Object> params = new HashMap<>();
      params.put("cid", "TC0ONETIME");
      params.put("tid", tid);
      params.put("partner_order_id", orderId);
      params.put("partner_user_id", userId);
      params.put("pg_token", pgToken);
      params.put("payload", productId);

      URI uri = UriComponentsBuilder
          .fromUriString(HOST)
          .path("/online/v1/payment/approve")
          .build()
          .toUri();

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

      ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Map.class);
      Map<String, Object> responseBody = response.getBody();
      log.info("Payment approved successfully: {}", responseBody);

//    kafkaProducer.sendSuccessMessage(responseBody);
    } finally {
      lock.unlock();
    }
  }

  //  @Transactional
//  @KafkaListener(topics = "payment-success-topic")
  public void paymentSuccess(String productId, String orderId, Integer quantity) {
    String stockKey = STOCK_KEY_PREFIX + productId;

    // Lua 스크립트를 사용하여 재고를 원자적으로 감소시킵니다.
    String luaScript =
        "local stock = redis.call('GET', KEYS[1]) " +
            "redis.call('SET', KEYS[1], stock) " +
            "if not stock then " +  // 만약 stock이 없으면
            "return -1 " +  // 재고 부족
            "end " +
            "if tonumber(stock) < tonumber(ARGV[1]) then " +
            "return -1 " + // 재고 부족
            "else " +
            "redis.call('DECRBY', KEYS[1], ARGV[1]) " +
            "return redis.call('GET', KEYS[1]) " +
            "end";

    Long result = redisTemplate.execute((RedisCallback<Long>) (connection) ->
        connection.eval(luaScript.getBytes(StandardCharsets.UTF_8), ReturnType.INTEGER,
            1, stockKey.getBytes(StandardCharsets.UTF_8),
            String.valueOf(quantity).getBytes(StandardCharsets.UTF_8)));

    if (result != null && result == -1) {
      throw new IllegalStateException("재고가 부족합니다.");
    }

    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

    order.success();
  }

  @Transactional
  @KafkaListener(topics = "payment-failure-topic")
  public void orderFailure(String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

    order.failure();
  }
}
