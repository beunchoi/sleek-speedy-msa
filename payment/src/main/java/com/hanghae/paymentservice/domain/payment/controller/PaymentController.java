package com.hanghae.paymentservice.domain.payment.controller;

import com.hanghae.paymentservice.domain.payment.service.PaymentService;
import com.hanghae.paymentservice.domain.payment.service.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-service")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

//  @GetMapping("/kakaopay/approval")
//  public ResponseEntity<String> approvePayment(@RequestParam("orderId") String orderId, @RequestParam("pg_token") String pgToken) {
//    try {
//      paymentService.approvePayment(pgToken, orderId);
//      return ResponseEntity.status(HttpStatus.OK).body("결제 완료");
//    } catch (Exception ex) {
//      paymentService.rollbackPayment(new PaymentFailedEvent(orderId));
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제가 취소되었습니다.");
//    }
//  }
//
//  @GetMapping("/kakaopay/fail")
//  public ResponseEntity<String> paymentFail(@RequestParam("orderId") String orderId) {
//    paymentService.rollbackPayment(new PaymentFailedEvent(orderId));
//    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제가 실패하였습니다.");
//  }
//
//  @GetMapping("/kakaopay/cancel")
//  public ResponseEntity<String> paymentCancel(@RequestParam("orderId") String orderId) {
//    paymentService.rollbackPayment(new PaymentFailedEvent(orderId));
//    return ResponseEntity.status(HttpStatus.OK).body("결제를 취소하셨습니다.");
//  }
}
