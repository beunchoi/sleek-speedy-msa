package com.hanghae.paymentservice.domain.payment.service;

import com.hanghae.paymentservice.domain.payment.event.OrderCreatedEvent;
import com.hanghae.paymentservice.domain.payment.event.PaymentFailedEvent;

public interface PaymentService {

  void completePayment(OrderCreatedEvent event);
  void savePayment(OrderCreatedEvent event);
  void rollbackPayment(PaymentFailedEvent event);

}
