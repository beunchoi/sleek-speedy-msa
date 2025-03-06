package com.hanghae.paymentservice.domain.payment.controller;

import com.hanghae.paymentservice.domain.payment.event.OrderCreatedEvent;
import com.hanghae.paymentservice.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payment")
public class PaymentInternalController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<Void> completePayment(@RequestBody OrderCreatedEvent event) {
    paymentService.completePayment(event);
    return ResponseEntity.status(200).build();
  }

}
