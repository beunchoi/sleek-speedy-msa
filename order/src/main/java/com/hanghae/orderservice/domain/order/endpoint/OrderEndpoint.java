package com.hanghae.orderservice.domain.order.endpoint;

import com.hanghae.orderservice.domain.order.dto.KakaoPaymentResponse;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.event.FailedPaymentEvent;
import com.hanghae.orderservice.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
@Slf4j
public class OrderEndpoint {

  private final OrderService orderService;

  @PostMapping("/orders")
  public Mono<ResponseEntity<KakaoPaymentResponse>> createOrder(@RequestHeader("X-User-Id") String userId, @RequestBody OrderRequestDto request) {
    return orderService.createOrder(request, userId)
        .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
  }

  @RabbitListener(queues = "${message2.queue.err.order}")
  public void handleFailedOrder(FailedPaymentEvent event) {
    orderService.rollbackOrder(event);
    log.info("롤백으로 주문 취소됨");
  }
}
