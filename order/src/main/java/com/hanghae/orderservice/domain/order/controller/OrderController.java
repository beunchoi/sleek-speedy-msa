package com.hanghae.orderservice.domain.order.controller;

import com.hanghae.orderservice.common.dto.ResponseMessage;
import com.hanghae.orderservice.common.util.ParseRequestUtil;
import com.hanghae.orderservice.domain.order.dto.OrderRequestDto;
import com.hanghae.orderservice.domain.order.dto.OrderResponseDto;
import com.hanghae.orderservice.domain.order.dto.ReturnResponseDto;
import com.hanghae.orderservice.domain.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<ResponseMessage> createOrder(
      HttpServletRequest request,
      @RequestParam("productId") String productId,
      @RequestBody OrderRequestDto requestDto) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    OrderResponseDto savedOrder = orderService.createOrder(requestDto, productId, userId);

    ResponseMessage message = ResponseMessage.builder()
        .data(savedOrder)
        .statusCode(201)
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @GetMapping
  public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(HttpServletRequest request) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    List<OrderResponseDto> responses = orderService.getOrdersByUserId(userId);

    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @PatchMapping("/{orderId}/cancel")
  public ResponseEntity<ResponseMessage> cancelOrder(HttpServletRequest request, @PathVariable("orderId") String orderId) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    OrderResponseDto response = orderService.cancelOrder(userId, orderId);

    ResponseMessage message = ResponseMessage.builder()
        .data(response)
        .statusCode(200)
        .resultMessage("주문이 취소되었습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @PatchMapping("/{orderId}/return")
  public ResponseEntity<ResponseMessage> requestProductReturn(HttpServletRequest request,
      @PathVariable("orderId") String orderId) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    ReturnResponseDto response = orderService.requestProductReturn(userId, orderId);

    ResponseMessage message = ResponseMessage.builder()
        .data(response)
        .statusCode(200)
        .resultMessage("상품 반품이 요청되었습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

//  @PatchMapping("/{orderId}/failure")
//  public ResponseEntity<OrderResponseDto> updateStatusToFail(@PathVariable("orderId") String orderId) {
//    OrderResponseDto response = orderService.updateStatusToFail(orderId);
//    return ResponseEntity.status(HttpStatus.OK).body(response);
//  }
//
//  @PatchMapping("/{orderId}/success")
//  public ResponseEntity<OrderResponseDto> updateStatusToSuccess(@PathVariable("orderId") String orderId) {
//    OrderResponseDto response = orderService.updateStatusToSuccess(orderId);
//    return ResponseEntity.status(HttpStatus.OK).body(response);
//  }

}
