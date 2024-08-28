package com.hanghae.orderservice.global.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.orderservice.domain.order.service.OrderService;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
  private static final String HOST = "https://open-api.kakaopay.com";
  private final RestTemplate restTemplate = new RestTemplate();
  private final KafkaProducer kafkaProducer;
  private final OrderService orderService;
  private final RedisTemplate<String, String> redisTemplate;
  private static final String STOCK_KEY_PREFIX = "product:stock:";

  @KafkaListener(topics = "payment-topic")
  @Transactional
  public void paymentProcess(String kafkaMessage) {
    log.info("Kafka Message -> " + kafkaMessage);

    Map<Object, Object> map = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    try {
      map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    }

//    String productId = (String) map.get("productId");
//    Integer quantity = (Integer) map.get("quantity");
//
//    String stockKey = STOCK_KEY_PREFIX + productId;
//    String value = redisTemplate.opsForValue().get(stockKey);
//    Integer stock = value != null ? Integer.parseInt(value) : 0;

    // HTTP 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "SECRET_KEY " + "DEVCFD942EFA3112904ED6632AC7F492D0E4BC34"); // 카카오 API 키
    headers.add("Content-Type", "application/json");

    // 결제 준비 API 요청 데이터 설정
    Map<String, Object> params = new HashMap<>();
    params.put("cid", "TC0ONETIME"); // 가맹점 코드
    params.put("partner_order_id", map.get("orderId")); // 가맹점 주문번호
    params.put("partner_user_id", map.get("userId")); // 가맹점 회원 ID
    params.put("item_name", map.get("productId")); // 상품명
    params.put("quantity", map.get("quantity")); // 상품 수량
    params.put("total_amount", map.get("totalPrice")); // 상품 총액
    params.put("tax_free_amount", "0"); // 비과세 금액
    params.put("approval_url", "http://localhost:8080/order-service/kakaopay/approval"); // 결제 성공 시 리다이렉트 URL
    params.put("fail_url", "http://localhost:8080/order-service/kakaopay/fail"); // 결제 실패 시 리다이렉트 URL
    params.put("cancel_url", "http://localhost:8080/order-service/kakaopay/cancel"); // 결제 취소 시 리다이렉트 URL

    // URI 설정
    URI uri = UriComponentsBuilder
        .fromUriString(HOST)
        .path("/online/v1/payment/ready")
        .build()
        .toUri();

    // HTTP 요청 보내기
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

    ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Map.class);
    String tid = response.getBody().get("tid").toString();
    String orderId = map.get("orderId").toString();
    String userId = map.get("userId").toString();
    String productId = map.get("productId").toString();

    redisTemplate.opsForValue().set("tid", tid);
    redisTemplate.opsForValue().set("orderId", orderId);
    redisTemplate.opsForValue().set("userId", userId);
    redisTemplate.opsForValue().set("productId", productId);
    log.info(response.getBody().get("next_redirect_pc_url").toString());
  }

//  @KafkaListener(topics = "payment-confirmation-topic")
//  public void handleOrderConfirmation(String kafkaMessage) throws JsonProcessingException {
//    ObjectMapper objectMapper = new ObjectMapper();
//    JsonNode rootNode = objectMapper.readTree(kafkaMessage);
//    JsonNode requestNode = rootNode.path("request");
//    String productId = requestNode.path("productId").asText();
//    String orderId = requestNode.path("orderId").asText();
//    Integer quantity = requestNode.path("quantity").asInt();
//
//    if (kafkaMessage.contains("approved_at")) {
//      // 결제 성공 시 재고 감소 및 주문 확정
//      orderService.orderSuccess(productId, quantity, orderId);
//    } else {
//      // 결제 실패 시 주문 취소
//      orderService.orderFailure(orderId);
//    }
//  }
}
