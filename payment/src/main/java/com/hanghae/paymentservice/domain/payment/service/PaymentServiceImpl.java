package com.hanghae.paymentservice.domain.payment.service;

import com.hanghae.paymentservice.domain.payment.entity.Payment;
import com.hanghae.paymentservice.domain.payment.event.OrderCreatedEvent;
import com.hanghae.paymentservice.domain.payment.event.PaymentFailedEvent;
import com.hanghae.paymentservice.domain.payment.event.PaymentSuccessEvent;
import com.hanghae.paymentservice.domain.payment.producer.PaymentEventProducer;
import com.hanghae.paymentservice.domain.payment.repository.PaymentRepository;
import com.hanghae.paymentservice.domain.payment.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private final PaymentEventProducer paymentEventProducer;
  private final PaymentRepository paymentRepository;
  private final StockRepository stockRepository;

  @Transactional
  @Override
  public void completePayment(OrderCreatedEvent event) {
    savePayment(event);

    paymentEventProducer.publish(new PaymentSuccessEvent
        (event.getProductId(), event.getOrderId(), event.getQuantity()));
  }

  @Override
  @Transactional
  public void savePayment(OrderCreatedEvent event) {
    try {
      stockRepository.getAndDecreaseStock(event.getProductId(), event.getQuantity());

      Payment payment = new Payment(event);
      paymentRepository.save(payment);
    } catch (Exception e) {
      rollbackPayment(new PaymentFailedEvent(
          event.getProductId(),
          event.getOrderId(),
          event.getQuantity()
      ));
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
