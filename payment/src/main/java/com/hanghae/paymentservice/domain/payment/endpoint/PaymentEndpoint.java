package com.hanghae.paymentservice.domain.payment.endpoint;

import com.hanghae.paymentservice.domain.payment.event.FailedPaymentEvent;
import com.hanghae.paymentservice.domain.payment.event.FailedStockUpdateEvent;
import com.hanghae.paymentservice.domain.payment.event.SavePaymentEvent;
import com.hanghae.paymentservice.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class PaymentEndpoint {

  private final PaymentService paymentService;

  @RabbitListener(queues = "${message.queue.payment}")
  public void handlePaymentQueue(SavePaymentEvent event) {
    paymentService.savePayment(event);
  }

  @RabbitListener(queues = "${message2.queue.err.payment}")
  public void handleFailedPayment(FailedStockUpdateEvent event) {
    FailedPaymentEvent paymentEvent = new FailedPaymentEvent(
        event.getProductId(),
        event.getOrderId(),
        event.getQuantity()
    );

    paymentService.rollbackPayment(paymentEvent);
    log.info("주문 취소 처리됨: {}", event.getOrderId());
  }
}
