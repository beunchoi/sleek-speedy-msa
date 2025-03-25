package com.hanghae.paymentservice.domain.payment.service;

import com.hanghae.paymentservice.domain.payment.entity.Payment;
import com.hanghae.paymentservice.domain.payment.event.OrderCreatedEvent;
import com.hanghae.paymentservice.domain.payment.event.PaymentFailedEvent;
import com.hanghae.paymentservice.domain.payment.event.PaymentSuccessEvent;
import com.hanghae.paymentservice.domain.payment.producer.PaymentEventProducer;
import com.hanghae.paymentservice.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class PaymentServiceImpl implements PaymentService {

  private final PaymentEventProducer paymentEventProducer;
  private final PaymentRepository paymentRepository;

  @Transactional
  @Override
  public void completePayment(OrderCreatedEvent event) {
    try {
      paymentRepository.save(new Payment(event));

      paymentEventProducer.publish(new PaymentSuccessEvent
          (event.getProductId(), event.getOrderId(), event.getQuantity()));
    } catch (Exception e) {
      rollbackPayment(new PaymentFailedEvent(
          event.getProductId(), event.getOrderId(), event.getQuantity()));
    }
  }

  @Transactional
  @Override
  public void rollbackPayment(PaymentFailedEvent event) {
    Payment payment = paymentRepository.findByOrderId(event.getOrderId())
        .orElse(null);

    if (payment != null) {
      payment.deletePayment();
    }

    paymentEventProducer.publishPaymentFailedEvent(event);
    log.info("Payment Rollback");
  }

}
