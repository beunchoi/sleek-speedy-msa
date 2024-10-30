package com.hanghae.paymentservice.domain.payment.service;

import com.hanghae.paymentservice.domain.payment.dto.OrderResponseDto;
import com.hanghae.paymentservice.domain.payment.entity.Payment;
import com.hanghae.paymentservice.domain.payment.event.FailedPaymentEvent;
import com.hanghae.paymentservice.domain.payment.event.SavePaymentEvent;
import com.hanghae.paymentservice.domain.payment.event.SaveProductStockEvent;
import com.hanghae.paymentservice.domain.payment.repository.PaymentRepository;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
  private final RedisTemplate<String, String> redisTemplate;
  private final RedissonClient redissonClient;
  private final RabbitTemplate rabbitTemplate;
  private final WebClient webClient;
  private final PaymentRepository paymentRepository;
  private static final String STOCK_KEY_PREFIX = "product:stock:";
  private static final String HOST = "https://open-api.kakaopay.com";
  private static final String PORT = "http://localhost:8080";
  @Value("${secret.key}")
  private String secretKey;
  @Value("${message.queue.product}")
  private String queueProduct;
  @Value("${message2.err.exchange}")
  private String exchangeErr;
  @Value("${message2.queue.err.order}")
  private String queueErrOrder;

  @Transactional
  public void approvePayment(String pgToken, String orderId) {
    String lockKey = "payment:lock:";
    RLock lock = redissonClient.getLock(lockKey);

    try {
      boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
      if (!isLocked) {
        log.error("락을 얻지 못했습니다.");
        throw new RuntimeException("락을 얻지 못했습니다.");
      }

      Payment payment = paymentRepository.findByOrderId(orderId)
          .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

      String tid = payment.getTid();
      String userId = payment.getUserId();
      String productId = payment.getProductId();
      String quantity = payment.getQuantity();

      this.successPayment(productId, orderId, Integer.valueOf(quantity));

      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "SECRET_KEY " + secretKey); // 실제 Secret Key를 사용해야 합니다.
      headers.add("Content-Type", "application/json");

      Map<String, Object> params = new HashMap<>();
      params.put("cid", "TC0ONETIME");
      params.put("tid", tid);
      params.put("partner_order_id", orderId);
      params.put("partner_user_id", userId);
      params.put("pg_token", pgToken);
      params.put("payload", productId);

      webClient.post()
          .uri(HOST + "/online/v1/payment/approve")
          .headers(httpHeaders -> httpHeaders.addAll(headers))
          .bodyValue(params)
          .retrieve()
          .bodyToMono(Map.class)
          .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
          .doOnError(ex -> log.error("결제 승인 요청 실패: " + ex.getMessage()))
          .subscribe();

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
      this.rollbackPayment(new FailedPaymentEvent(
          productId, orderId, quantity));
      return;
    }

    // 문자열을 정수로 변환하여 재고와 구매 수량 비교
    int stockValue = Integer.parseInt(stock);
    if (stockValue < quantity) {
      // 재고 부족
      this.rollbackPayment(new FailedPaymentEvent(
          productId, orderId, quantity));
    } else {
      // 재고 감소 처리
      redisTemplate.opsForValue().decrement(stockKey, quantity);

      this.orderSuccess(orderId);

      rabbitTemplate.convertAndSend(queueProduct,
          new SaveProductStockEvent(productId, orderId, Integer.valueOf(quantity)));
    }
  }

  public void orderSuccess(String orderId) {
    webClient.put()
        .uri(PORT + "/order-service/{orderId}/orders/success", orderId)
        .retrieve()
        .bodyToMono(OrderResponseDto.class)
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
        .subscribe();
  }

  public void savePayment(SavePaymentEvent event) {
    Payment payment = new Payment(
        event.getTid(),
        event.getOrderId(),
        event.getUserId(),
        event.getProductId(),
        event.getQuantity()
    );

    try {
      paymentRepository.save(payment);
    } catch (Exception e) {
      this.rollbackPayment(new FailedPaymentEvent(
          event.getProductId(),
          event.getOrderId(),
          Integer.parseInt(event.getQuantity())
      ));
    }
  }

  @Transactional
  public void rollbackPayment(FailedPaymentEvent event) {
    log.info("Payment Rollback");
    Payment payment = paymentRepository.findByOrderId(event.getOrderId())
        .orElse(null);

    if (payment != null) {
      payment.deletePayment();
    }

    rabbitTemplate.convertAndSend(exchangeErr, queueErrOrder, event);
  }
}
