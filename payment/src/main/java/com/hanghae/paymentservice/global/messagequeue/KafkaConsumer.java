package com.hanghae.paymentservice.global.messagequeue;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
  private static final String HOST = "https://open-api.kakaopay.com";
  private final RedisTemplate<String, String> redisTemplate;
  private final WebClient webClient;
  @Value("${secret.key}")
  private String secretKey;

  private AtomicInteger successCounter = new AtomicInteger(0);

  public Mono<Void> requestPayment(String orderId, String userId, String productId, String quantity, String totalPrice) {

    // HTTP 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "SECRET_KEY " + secretKey); // 카카오 API 키
    headers.add("Content-Type", "application/json");

    // 결제 준비 API 요청 데이터 설정
    Map<String, Object> params = new HashMap<>();
    params.put("cid", "TC0ONETIME"); // 가맹점 코드
    params.put("partner_order_id", orderId); // 가맹점 주문번호
    params.put("partner_user_id", userId); // 가맹점 회원 ID
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
          // 성공 시 카운터를 증가시키고 로그 출력
          int successCount = successCounter.incrementAndGet();
          log.info("결제 요청 성공! {}번째 성공. 응답 데이터: {}", successCount, response);
        })
        .flatMap(response -> {
          // 결제 API 응답 후 Redis에 결제 정보 저장
          String tid = response.get("tid").toString();
          redisTemplate.opsForValue().set("tid", tid);
          redisTemplate.opsForValue().set("orderId", orderId);
          redisTemplate.opsForValue().set("userId", userId);
          redisTemplate.opsForValue().set("productId", productId);
          redisTemplate.opsForValue().set("quantity", quantity);

          log.info("결제 정보가 Redis에 저장되었습니다. TID: {}", tid);
          log.info("결제 URL: {}", response.get("next_redirect_pc_url").toString());
          return Mono.empty(); // Void 반환
        });
  }
}
