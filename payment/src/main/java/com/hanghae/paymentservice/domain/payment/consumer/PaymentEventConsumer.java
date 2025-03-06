package com.hanghae.paymentservice.domain.payment.consumer;

import com.hanghae.paymentservice.domain.payment.config.RabbitMQConfig;
import com.hanghae.paymentservice.domain.payment.event.PaymentFailedEvent;
import com.hanghae.paymentservice.domain.payment.event.StockUpdateFailedEvent;
import com.hanghae.paymentservice.domain.payment.event.OrderCreatedEvent;
import com.hanghae.paymentservice.domain.payment.service.PaymentService;
import com.hanghae.paymentservice.domain.payment.service.PaymentServiceImpl;
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
    log.info("오더 이벤트 소비: {}", event);
    paymentService.completePayment(event);
  }

  @RabbitListener(queues = RabbitMQConfig.queueErrPayment)
  public void handleFailedEvent(StockUpdateFailedEvent event) {
    log.info("재고 업데이트 실패 이벤트 소비: {}", event);
    PaymentFailedEvent paymentEvent = new PaymentFailedEvent(
        event.getProductId(),
        event.getOrderId(),
        event.getQuantity()
    );

    paymentService.rollbackPayment(paymentEvent);
  }

}
