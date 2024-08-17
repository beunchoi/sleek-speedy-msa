package com.hanghae.sleekspeedy.domain.order.controller;

import com.hanghae.sleekspeedy.domain.order.dto.OrderRequestDto;
import com.hanghae.sleekspeedy.domain.order.dto.OrderResponseDto;
import com.hanghae.sleekspeedy.domain.order.service.OrderService;
import com.hanghae.sleekspeedy.global.jwt.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final JwtUtil jwtUtil;
  private final Environment env;

  @GetMapping("/health_check")
  public String status() {
    return String.format("주문 서비스 정상 작동 중입니다. 포트 번호 : %s", env.getProperty("local.server.port"));
  }

  @PostMapping("/{userId}/orders")
  public ResponseEntity<OrderResponseDto> createOrder(@PathVariable String userId, @RequestBody OrderRequestDto request) {

    OrderResponseDto response = orderService.createOrder(request, userId);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{userId}/orders")
  public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@PathVariable String userId) {
    List<OrderResponseDto> response = orderService.getOrdersByUserId(userId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

//  @GetMapping("/orders/{orderId}")
//  public List<OrderProductResponse> getOrderProducts(@PathVariable Long orderId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//    return orderService.getOrderProducts(orderId, userDetails.getUser());
//  }
//
//  @PutMapping("/orders/{orderProductId}/cancel")
//  public ResponseEntity<ResponseDto> cancelOrderProduct(@PathVariable Long orderProductId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//    orderService.cancelOrderProduct(orderProductId, userDetails.getUser());
//    return ResponseEntity.ok().body(ResponseDto.success(200));
//  }
//
//  @PutMapping("/orders/{orderProductId}/return")
//  public ResponseEntity<ResponseDto> returnOrderProduct(@PathVariable Long orderProductId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//    orderService.returnOrderProduct(orderProductId, userDetails.getUser());
//    return ResponseEntity.ok().body(ResponseDto.success(200));
//  }
}
