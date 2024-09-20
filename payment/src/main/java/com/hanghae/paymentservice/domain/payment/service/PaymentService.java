package com.hanghae.paymentservice.domain.payment.service;

import com.hanghae.paymentservice.domain.payment.dto.OrderResponseDto;
import com.hanghae.paymentservice.domain.payment.dto.PaymentResponseDto;
import com.hanghae.paymentservice.domain.payment.entity.Payment;
import com.hanghae.paymentservice.domain.payment.repository.PaymentRepository;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
  private final OrderRepository orderRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final RedissonClient redissonClient;
  private final RabbitTemplate rabbitTemplate;
  private final WebClient webClient;
  private final PaymentRepository paymentRepository;
  private static final String STOCK_KEY_PREFIX = "product:stock:";
  private static final String HOST = "https://open-api.kakaopay.com";
  @Value("${secret.key}")
  private String secretKey;

  public Mono<ResponseEntity<Map<String, Object>>> requestPayment(String orderId, String userId,
      String productId, String quantity, String totalPrice) {

    // HTTP 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "SECRET_KEY " + secretKey); // 카카오 API 키
    headers.add("Content-Type", "application/json");

    // 결제 준비 API 요청 데이터 설정
    Map<String, Object> params = new HashMap<>();
    params.put("cid", "TC0ONETIME"); // 가맹점 코드
    params.put("partner_order_id", orderId); // 주문번호
    params.put("partner_user_id", userId); // 회원 ID
    params.put("item_name", productId); // 상품명
    params.put("quantity", quantity); // 상품 수량
    params.put("total_amount", totalPrice); // 상품 총액
    params.put("tax_free_amount", "0"); // 비과세 금액
    params.put("approval_url",
        "http://localhost:8080/order-service/kakaopay/approval"); // 결제 성공 시 리다이렉트 URL
    params.put("fail_url", "http://localhost:8080/order-service/kakaopay/fail?orderId="
        + orderId); // 결제 실패 시 리다이렉트 URL
    params.put("cancel_url", "http://localhost:8080/order-service/kakaopay/cancel?orderId="
        + orderId); // 결제 취소 시 리다이렉트 URL

    return webClient.post()
        .uri(HOST + "/online/v1/payment/ready")
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .bodyValue(params)
        .retrieve()
        .bodyToMono(Map.class)  // 응답을 비동기로 처리
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))) // 1초 간격으로 최대 3번 재시도
        .doOnError(ex -> log.error("결제 요청 실패: " + ex.getMessage())) // 에러 처리
        .doOnSuccess(response -> {
          log.info("결제 요청 성공!");
        })
        .flatMap(response -> {
          // 결제 API 응답 후 Redis에 결제 정보 저장
          String tid = response.get("tid").toString();
          String pgToken = response.get("pg_token").toString();

          try {
            PaymentResponseDto payment = new PaymentResponseDto(tid, orderId, userId, productId, quantity, pgToken);
            rabbitTemplate.convertAndSend("payment-queue", payment);
          } catch (Exception e) {
            log.error("메시지 전송 실패");
            return Mono.error(new Exception("메시지 전송 실패"));
          }

          String nextRedirectPcUrl = response.get("next_redirect_pc_url").toString();
          Map<String, Object> result = new HashMap<>();
          result.put("next_redirect_pc_url", nextRedirectPcUrl);
          result.put("pg_token", pgToken);

          log.info("결제 URL: {}", response.get("next_redirect_pc_url").toString());
          return Mono.just(ResponseEntity.ok(result));
        });
  }

  @Transactional
  public void approvePayment(String pgToken) {
    String lockKey = "payment:lock:";
    RLock lock = redissonClient.getLock(lockKey);

    try {
      boolean isLocked = lock.tryLock(7, 3, TimeUnit.SECONDS);
      if (!isLocked) {
        log.error("락을 얻지 못했습니다.");
        throw new RuntimeException("락을 얻지 못했습니다.");
      }

      Payment payment = paymentRepository.findAllByPgToken(pgToken)
          .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

      String tid = payment.getTid();
      String orderId = payment.getOrderId();
      String userId = payment.getUserId();
      String productId = payment.getProductId();
      String quantity = payment.getQuantity();

      this.successPayment(productId, orderId, Integer.valueOf(quantity));

//      HttpHeaders headers = new HttpHeaders();
//      headers.add("Authorization", "SECRET_KEY " + secretKey); // 실제 Secret Key를 사용해야 합니다.
//      headers.add("Content-Type", "application/json");
//
//      Map<String, Object> params = new HashMap<>();
//      params.put("cid", "TC0ONETIME");
//      params.put("tid", tid);
//
//
//      params.put("partner_order_id", orderId);
//      params.put("partner_user_id", userId);
//      params.put("pg_token", pgToken);
//      params.put("payload", productId);
//
//      URI uri = UriComponentsBuilder
//          .fromUriString(HOST)
//          .path("/online/v1/payment/approve")
//          .build()
//          .toUri();
//
//      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
//
//      ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Map.class);
//      Map<String, Object> responseBody = response.getBody();
//      log.info("Payment approved successfully: {}", responseBody);
    } catch (InterruptedException e) {
      log.error("락 사용 중 인터럽트 발생: {}", e.getMessage());
      throw new RuntimeException(e);
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  @Transactional
  public void successPayment(String productId, String orderId, Integer quantity) {
    String stockKey = STOCK_KEY_PREFIX + productId;

    String stock = redisTemplate.opsForValue().get(stockKey);

    if (stock == null) {
      // 재고가 없는 경우 처리
      Order order = orderRepository.findByOrderId(orderId)
          .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
      order.failure();
      return;
    }

    // 문자열을 정수로 변환하여 재고와 구매 수량 비교
    int stockValue = Integer.parseInt(stock);
    if (stockValue < quantity) {
      // 재고 부족
      Order order = orderRepository.findByOrderId(orderId)
          .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
      order.failure();
        throw new IllegalStateException("재고가 부족합니다.");
    } else {
      // 재고 감소 처리
      redisTemplate.opsForValue().decrement(stockKey, quantity);

//      // Lua 스크립트를 사용하여 재고를 원자적으로 감소시킵니다.
//    String luaScript =
//        "local stock = redis.call('GET', KEYS[1]) " +
//            "redis.call('SET', KEYS[1], stock) " +
//            "if not stock then " +  // 만약 stock이 없으면
//            "return -1 " +  // 재고 부족
//            "end " +
//            "if tonumber(stock) < tonumber(ARGV[1]) then " +
//            "return -1 " + // 재고 부족
//            "else " +
//            "redis.call('DECRBY', KEYS[1], ARGV[1]) " +
//            "return redis.call('GET', KEYS[1]) " +
//            "end";
//
//    Long result = redisTemplate.execute((RedisCallback<Long>) (connection) ->
//        connection.eval(luaScript.getBytes(StandardCharsets.UTF_8), ReturnType.INTEGER,
//            1, stockKey.getBytes(StandardCharsets.UTF_8),
//            String.valueOf(quantity).getBytes(StandardCharsets.UTF_8)));

      Order order = orderRepository.findByOrderId(orderId)
          .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

//    if (result != null && result == -1) {
//      order.failure();
//      throw new IllegalStateException("재고가 부족합니다.");
//    } else {
//      order.success();
//      rabbitMQProducer.sendToProduct("product-topic", new OrderResponseDto(productId, Integer.valueOf(quantity)));
//    }
      order.success();
      rabbitTemplate.convertAndSend("product-queue",
          new OrderResponseDto(productId, Integer.valueOf(quantity)));
//      Product product = productRepository.findByProductId(productId)
//          .orElseThrow(() -> new IllegalArgumentException("s"));
//
//      product.updateStock(product.getStock() - quantity);
    }
  }

  @RabbitListener(queues = "payment-queue")
  public void savePayment(PaymentResponseDto responseDto) {

    String tid = responseDto.getTid();
    String orderId = responseDto.getOrderId();
    String userId = responseDto.getUserId();
    String productId = responseDto.getProductId();
    String quantity = responseDto.getQuantity();
    String pgToken = responseDto.getPgToken();

    paymentRepository.save(new Payment(tid, orderId, userId, productId, quantity, pgToken));
  }

  @Transactional
  public void orderFailure(String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

    order.failure();
  }
}
