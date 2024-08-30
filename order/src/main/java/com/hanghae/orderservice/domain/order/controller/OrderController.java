package com.hanghae.orderservice.domain.order.controller;

import com.hanghae.orderservice.domain.order.dto.CancelResponse;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.dto.ReturnRequestResponse;
import com.hanghae.orderservice.domain.order.service.OrderService;
import com.hanghae.orderservice.domain.order.service.PaymentService;
import com.hanghae.orderservice.global.messagequeue.KafkaProducer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final Environment env;
  private final PaymentService paymentService;

  @GetMapping("/health_check")
  public String status() {
    return String.format("주문 서비스 정상 작동 중입니다. 포트 번호 : %s", env.getProperty("local.server.port"));
  }

  @PostMapping("/{userId}/orders")
  public ResponseEntity<OrderResponseDto> createOrder(@PathVariable String userId, @RequestBody OrderRequestDto request) {

    OrderResponseDto response = orderService.createOrder(request, userId);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/kakaopay/approval")
  public ResponseEntity<String> approvePayment(
      @RequestParam("pg_token") String pgToken) {

    paymentService.approvePayment(pgToken);
    return ResponseEntity.ok("Payment approved successfully");
  }

  @GetMapping("/kakaopay/fail")
  public ResponseEntity<String> paymentFail(@RequestParam("orderId") String orderId) {
    paymentService.orderFailure(orderId);
    return ResponseEntity.badRequest().body("Payment failed");
  }

  @GetMapping("/kakaopay/cancel")
  public ResponseEntity<String> paymentCancel(@RequestParam("orderId") String orderId) {
    paymentService.orderFailure(orderId);
    return ResponseEntity.ok("Payment canceled");
  }

  @PutMapping("/{userId}/{orderId}/cancel")
  public ResponseEntity<CancelResponse> cancelOrder(@PathVariable String userId, @PathVariable String orderId) {

    CancelResponse response = orderService.cancelOrder(userId, orderId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/{userId}/{orderId}/return")
  public ResponseEntity<ReturnRequestResponse> requestReturn(@PathVariable String userId,
      @PathVariable String orderId) {

    ReturnRequestResponse response = orderService.requestReturn(userId, orderId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{userId}/orders")
  public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@PathVariable String userId) {
    List<OrderResponseDto> response = orderService.getOrdersByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
