package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.common.dto.ResponseMessage;
import com.hanghae.userservice.common.util.ParseRequestUtil;
import com.hanghae.userservice.domain.user.dto.paymentmethod.PaymentMethodRequestDto;
import com.hanghae.userservice.domain.user.dto.paymentmethod.PaymentMethodResponseDto;
import com.hanghae.userservice.domain.user.service.PaymentMethodService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-method")
@RequiredArgsConstructor
public class PaymentMethodController {

  private final PaymentMethodService paymentMethodService;

  @PostMapping
  public ResponseEntity<ResponseMessage> createPaymentMethod(HttpServletRequest request,
      @RequestBody PaymentMethodRequestDto requestDto) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    paymentMethodService.createPaymentMethod(userId, requestDto);

    ResponseMessage message = ResponseMessage.builder()
        .statusCode(201)
        .resultMessage("결제 수단 등록")
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @GetMapping
  public ResponseEntity<ResponseMessage> getMyAllPaymentMethod(HttpServletRequest request) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    List<PaymentMethodResponseDto> responses = paymentMethodService.getMyAllPaymentMethod(userId);

    ResponseMessage message = ResponseMessage.builder()
        .data(responses)
        .statusCode(200)
        .resultMessage("결제 수단 목록 조회")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @DeleteMapping("/{paymentMethodId}")
  public ResponseEntity<?> deletePaymentMethod(HttpServletRequest request,
      @PathVariable("paymentMethodId") String paymentMethodId) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    paymentMethodService.deletePaymentMethod(paymentMethodId);

    ResponseMessage message = ResponseMessage.builder()
        .statusCode(200)
        .resultMessage("결제 수단 삭제")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

}
