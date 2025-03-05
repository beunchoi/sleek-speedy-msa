package com.hanghae.orderservice.domain.order.consumer;

import com.hanghae.orderservice.domain.order.config.RabbitMQConfig;
import com.hanghae.orderservice.domain.order.event.PaymentFailedEvent;
import com.hanghae.orderservice.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j(topic = "OrderEventConsumer")
@Component
public class OrderEventConsumer {

  private final OrderService orderService;

  @RabbitListener(queues = RabbitMQConfig.queueErrOrder)
  public void handleFailedEvent(PaymentFailedEvent event) {
    log.info("결제 실패 이벤트 소비: {}", event);
    orderService.rollbackOrder(event);
  }

}
