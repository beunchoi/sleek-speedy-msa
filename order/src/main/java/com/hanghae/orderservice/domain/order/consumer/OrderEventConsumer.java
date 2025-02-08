package com.hanghae.orderservice.domain.order.consumer;

import com.hanghae.orderservice.domain.order.config.RabbitMQConfig;
import com.hanghae.orderservice.domain.order.event.PaymentFailedEvent;
import com.hanghae.orderservice.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

  private final OrderService orderService;

  @RabbitListener(queues = RabbitMQConfig.queueErrOrder)
  public void handleFailedOrder(PaymentFailedEvent event) {
    orderService.rollbackOrder(event);
    log.info("롤백으로 주문 취소됨");
  }
}
