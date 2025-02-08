package com.hanghae.orderservice.domain.order.producer;

import com.hanghae.orderservice.domain.order.config.RabbitMQConfig;
import com.hanghae.orderservice.domain.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "OrderEventProducer")
public class OrderEventProducer {

  private final RabbitTemplate rabbitTemplate;

  public void publish(OrderCreatedEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.exchange, RabbitMQConfig.queuePayment,
        event);
    log.info("주문 생성 이벤트 전송");
  }

}
