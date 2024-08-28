package com.hanghae.orderservice.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.orderservice.domain.order.dto.CancelResponse;
import com.hanghae.orderservice.domain.order.dto.ReturnRequestResponse;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.global.messagequeue.KafkaProducer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final KafkaProducer kafkaProducer;
  private final RedissonClient redissonClient;
  private final RestTemplate restTemplate = new RestTemplate();
  private static final String STOCK_KEY_PREFIX = "product:stock:";
  private static final String HOST = "https://open-api.kakaopay.com";

  public OrderResponseDto createOrder(OrderRequestDto request, String userId) {
    String orderId = UUID.randomUUID().toString();
    Integer totalPrice = request.getPrice() * request.getQuantity();
    OrderStatus status = OrderStatus.PENDING;

    Order order = orderRepository.save(new Order(request, orderId, totalPrice, userId, status));

    return new OrderResponseDto(order);
  }
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
      try {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> successPayload = new HashMap<>();
        successPayload.put("response", responseBody);
        String successMessage = mapper.writeValueAsString(successPayload);
        this.orderSuccess(successMessage);
        log.info("Payment success message sent to Kafka: {}", successMessage);
      } catch (JsonProcessingException ex) {
        log.error("Failed to send payment success message to Kafka", ex);
      }
    } finally {
      lock.unlock();
    }
  }

//  @Transactional
//  @KafkaListener(topics = "payment-success-topic")
  public void orderSuccess(String kafkaMessage) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(kafkaMessage);
    JsonNode responeNode = rootNode.path("response");
    String productId = responeNode.path("payload").asText();
    String orderId = responeNode.path("partner_order_id").asText();
    Integer quantity = responeNode.path("quantity").asInt();

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

  @Transactional
  public CancelResponse cancelOrder(String userId, String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("주문 상품이 존재하지 않습니다."));

    if (!order.getUserId().equals(userId)) {
      throw new IllegalStateException("주문을 취소할 수 없습니다.");
    }

    if (order.getStatus() == OrderStatus.ORDERED) {
      order.cancel();
//      product.incrementStock();
    } else {
      throw new IllegalStateException("배송 전의 상품만 취소할 수 있습니다.");
    }

    return new CancelResponse(order);
  }

  @Transactional
  public ReturnRequestResponse requestReturn(String userId, String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("주문 상품이 존재하지 않습니다."));

    if (!order.getUserId().equals(userId)) {
      throw new IllegalStateException("반품할 수 없습니다.");
    }

    if (order.getStatus() == OrderStatus.DELIVERED && LocalDate.now().isBefore(order.getDeliveredDate().plusDays(1))) {
      order.requestReturn();  // 반품 처리
    } else {
      throw new IllegalStateException("반품 가능한 기간이 지났거나, 배송 완료 상태가 아닙니다.");
    }

    return new ReturnRequestResponse(order);
  }

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
  @Transactional
  public void completeReturn() {
    List<Order> requestedOrders = orderRepository.findAllByStatus(
        OrderStatus.RETURN_REQUESTED);

    for (Order order : requestedOrders) {
      if (order.getStatus() == OrderStatus.RETURN_REQUESTED &&
          LocalDate.now().isAfter(order.getReturnRequestedDate().plusDays(1))) {
        order.completeReturn();
//        product.incrementStock();
      } else {
        throw new IllegalStateException("반품 처리 조건이 맞지 않습니다.");
      }
    }
  }

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
  @Transactional
  public void updateOrderStatus() {
    List<Order> orderList = orderRepository.findAllByStatus(
        OrderStatus.ORDERED);

    if (orderList != null && !orderList.isEmpty()) {
      for (Order order : orderList) {
        order.orderedToShipped();
      }
    }

    List<Order> shippedOrderList = orderRepository.findAllByStatus(
        OrderStatus.SHIPPED);

    if (shippedOrderList != null && !shippedOrderList.isEmpty()) {
      for (Order order : shippedOrderList) {
        order.shippedToDelivered();
      }
    }
  }
}
