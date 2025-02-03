package com.hanghae.orderservice.domain.order.endpoint;

import com.hanghae.orderservice.domain.order.event.FailedPaymentEvent;
import com.hanghae.orderservice.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RequiredArgsConstructor
@Slf4j
public class OrderEndpoint {

  private final OrderService orderService;

  @RabbitListener(queues = "${message2.queue.err.order}")
  public void handleFailedOrder(FailedPaymentEvent event) {
    orderService.rollbackOrder(event);
    log.info("롤백으로 주문 취소됨");
  }
}
