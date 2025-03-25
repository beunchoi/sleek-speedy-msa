package com.hanghae.productservice.domain.product.producer;

import com.hanghae.productservice.domain.product.config.RabbitMQConfig;
import com.hanghae.productservice.domain.product.event.StockCheckEvent;
import com.hanghae.productservice.domain.product.event.StockUpdateFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "ProductEventProducer")
public class ProductEventProducer {

  private final RabbitTemplate rabbitTemplate;

  public void publish(StockCheckEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.exchange, RabbitMQConfig.queueOrder, event);
    log.info("재고 확인 이벤트 전송");
  }

  public void publishFailedEvent(StockUpdateFailedEvent event) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.exchangeErr, RabbitMQConfig.queueErrPayment, event);
    log.info("재고 업데이트 실패 이벤트 전송");
  }

}
