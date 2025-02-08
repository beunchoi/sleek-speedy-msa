package com.hanghae.paymentservice.domain.payment.consumer;

import com.hanghae.paymentservice.domain.payment.config.RabbitMQConfig;
import com.hanghae.paymentservice.domain.payment.event.PaymentFailedEvent;
import com.hanghae.paymentservice.domain.payment.event.FailedStockUpdateEvent;
import com.hanghae.paymentservice.domain.payment.event.OrderCreatedEvent;
import com.hanghae.paymentservice.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j(topic = "PaymentEventConsumer")
@Component
public class PaymentEventConsumer {

  private final PaymentService paymentService;

  @RabbitListener(queues = RabbitMQConfig.queuePayment)
  public void handleEvent(OrderCreatedEvent event) {
    log.info("오더 이벤트 소비");
    paymentService.completePayment(event);
  }

  @RabbitListener(queues = RabbitMQConfig.queueErrPayment)
  public void handleFailedEvent(FailedStockUpdateEvent event) {
    PaymentFailedEvent paymentEvent = new PaymentFailedEvent(
        event.getProductId(),
        event.getOrderId(),
        event.getQuantity()
    );

//    paymentService.rollbackPayment(paymentEvent);
    log.info("주문 취소 처리됨: {}", event.getOrderId());
  }
}
