package com.hanghae.paymentservice.domain.payment.producer;

import com.hanghae.paymentservice.domain.payment.config.RabbitMQConfig;
import com.hanghae.paymentservice.domain.payment.event.PaymentFailedEvent;
import com.hanghae.paymentservice.domain.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "PaymentEventProducer")
public class PaymentEventProducer {

  private final RabbitTemplate rabbitTemplate;

  public void publish(PaymentSuccessEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.exchange, RabbitMQConfig.queueProduct, event);
    log.info("결제 성공 이벤트 전송");
  }

  public void publishPaymentFailedEvent(PaymentFailedEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.exchangeErr, RabbitMQConfig.queueErrOrder, event);
    log.info("결제 실패 이벤트 전송");
  }

}
