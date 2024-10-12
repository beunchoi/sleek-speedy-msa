package com.hanghae.orderservice.domain.order.controller;

import com.hanghae.orderservice.domain.order.dto.CancelResponse;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.dto.ReturnRequestResponse;
import com.hanghae.orderservice.domain.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PutMapping("/{orderId}/cancel")
  public ResponseEntity<CancelResponse> cancelOrder(@RequestHeader("X-User-Id") String userId, @PathVariable String orderId) {

    CancelResponse response = orderService.cancelOrder(userId, orderId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/{orderId}/return")
  public ResponseEntity<ReturnRequestResponse> requestReturn(@RequestHeader("X-User-Id") String userId,
      @PathVariable String orderId) {

    ReturnRequestResponse response = orderService.requestReturn(userId, orderId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/orders")
  public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@RequestHeader("X-User-Id") String userId) {
    List<OrderResponseDto> response = orderService.getOrdersByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/{orderId}/orders/failure")
  public ResponseEntity<OrderResponseDto> updateStatusToFail(@PathVariable String orderId) {
    OrderResponseDto response = orderService.updateStatusToFail(orderId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping("/{orderId}/orders/success")
  public ResponseEntity<OrderResponseDto> updateStatusToSuccess(@PathVariable String orderId) {
    OrderResponseDto response = orderService.updateStatusToSuccess(orderId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
