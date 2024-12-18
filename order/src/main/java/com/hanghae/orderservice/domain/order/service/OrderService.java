package com.hanghae.orderservice.domain.order.service;

import com.hanghae.orderservice.domain.order.dto.CancelResponse;
import com.hanghae.orderservice.domain.order.event.FailedPaymentEvent;
import com.hanghae.orderservice.domain.order.dto.KakaoPaymentResponse;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.dto.ProductResponseDto;
import com.hanghae.orderservice.domain.order.dto.ReturnRequestResponse;
import com.hanghae.orderservice.domain.order.event.SavePaymentEvent;
import com.hanghae.orderservice.domain.order.entity.Order;
import com.hanghae.orderservice.domain.order.entity.OrderStatus;
import com.hanghae.orderservice.domain.order.repository.OrderRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final WebClient webClient;
  private final RabbitTemplate rabbitTemplate;
  private static final String PORT = "http://localhost:8081";
  private static final String HOST = "https://open-api.kakaopay.com";
  @Value("${message.exchange}")
  private String exchange;
  @Value("${message.queue.payment}")
  private String queuePayment;
  @Value("${secret.key}")
  private String secretKey;

  @Transactional
  public Mono<KakaoPaymentResponse> createOrder(OrderRequestDto request, String userId) {
    String orderId = UUID.randomUUID().toString();
    String productId = request.getProductId();
    Integer totalPrice = request.getPrice() * request.getQuantity();
    OrderStatus status = OrderStatus.PENDING;

    // 구매 가능 시간 체크
    // 상품, 상품 재고 있는지 체크
    return checkProduct(productId)
        .flatMap(product -> {
          if (product.getStock() < request.getQuantity()) {
            return Mono.error(new IllegalArgumentException("상품 재고가 부족합니다."));
          }
          return Mono.fromCallable(() -> orderRepository.save(new Order(request, orderId, totalPrice, userId, status)))
              .subscribeOn(Schedulers.boundedElastic()) // 블로킹 작업 비동기 처리
              .flatMap(savedOrder ->
                  requestPayment(
                      savedOrder.getOrderId(),
                      savedOrder.getUserId(),
                      savedOrder.getProductId(),
                      String.valueOf(savedOrder.getQuantity()),
                      String.valueOf(savedOrder.getTotalPrice())
                  )
                      .map(response -> {
                        Map<String, Object> map = response.getBody();
                        return new KakaoPaymentResponse(
                            map.get("next_redirect_pc_url").toString(),
                            map.get("partner_order_id").toString()
                        );
                      })
              );
        });
  }

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
        "http://localhost:8083/payment-service/kakaopay/approval?orderId=" + orderId); // 결제 성공 시 리다이렉트 URL
    params.put("fail_url", "http://localhost:8083/payment-service/kakaopay/fail?orderId="
        + orderId); // 결제 실패 시 리다이렉트 URL
    params.put("cancel_url", "http://localhost:8083/payment-service/kakaopay/cancel?orderId="
        + orderId); // 결제 취소 시 리다이렉트 URL

    return webClient.post()
        .uri(HOST + "/online/v1/payment/ready")
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .bodyValue(params)
        .retrieve()
        .bodyToMono(Map.class)  // 응답을 비동기로 처리
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))) // 1초 간격으로 최대 3번 재시도
        .doOnError(ex -> log.error("결제 요청 실패: " + ex.getMessage())) // 에러 처리
        .flatMap(response -> {
          String tid = response.get("tid").toString();

          String nextRedirectPcUrl = response.get("next_redirect_pc_url").toString();
          Map<String, Object> result = new HashMap<>();
          result.put("next_redirect_pc_url", nextRedirectPcUrl);
          result.put("partner_order_id", orderId);

          rabbitTemplate.convertAndSend(exchange, queuePayment, new SavePaymentEvent(
              tid, orderId, userId, productId, quantity));

          log.info("결제 URL: {}", response.get("next_redirect_pc_url").toString());
          return Mono.just(ResponseEntity.ok(result));
        });
  }

  public Mono<ProductResponseDto> getProduct(String productId) {
    return webClient.get()
        .uri(PORT + "/product-service/{productId}", productId)
        .retrieve()
        .bodyToMono(ProductResponseDto.class)
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
  }

  public Mono<ProductResponseDto> checkProduct(String productId) {
    return getProduct(productId)
        .onErrorResume(e -> {
          log.error("상품을 찾을 수 없습니다. 상품ID: {}, 오류: {}", productId, e.getMessage());
          return Mono.error(new IllegalArgumentException("상품을 찾을 수 없습니다."));
        });
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
      webClient.put()
          .uri(PORT + "/product-service/{productId}/increment", order.getProductId())
          .retrieve()
          .bodyToMono(ProductResponseDto.class)
          .block();
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


  public List<OrderResponseDto> getOrdersByUserId(String userId) {
    List<Order> orderList = orderRepository.findAllByUserId(userId);

    return orderList.stream().map(OrderResponseDto::new).collect(Collectors.toList());
  }

  @Transactional
  public OrderResponseDto updateStatusToFail(String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

    order.failure();
    return new OrderResponseDto(order);
  }

  @Transactional
  public OrderResponseDto updateStatusToSuccess(String orderId) {
    Order order = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

    order.success();
    return new OrderResponseDto(order);
  }

  @Transactional
  public void rollbackOrder(FailedPaymentEvent event) {
    Order order = orderRepository.findByOrderId(event.getOrderId())
        .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

    order.failure();
  }
}
