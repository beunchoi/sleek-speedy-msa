package com.hanghae.productservice.domain.product.consumer;

import com.hanghae.productservice.domain.product.config.RabbitMQConfig;
import com.hanghae.productservice.domain.product.event.OrderFailedEvent;
import com.hanghae.productservice.domain.product.event.PaymentSuccessEvent;
import com.hanghae.productservice.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j(topic = "ProductEventConsumer")
@Component
public class ProductEventConsumer {

  private final ProductService productService;

  @RabbitListener(queues = RabbitMQConfig.queueProduct)
  public void handleEvent(PaymentSuccessEvent event) {
    log.info("결제 성공 이벤트 소비");
    productService.decreaseProductStock(event);
  }

  @RabbitListener(queues = RabbitMQConfig.queueErrOrder)
  public void handleFailEvent(OrderFailedEvent event) {
    log.info("주문 취소 이벤트 소비: {}", event);
    productService.restoreRedisStock(event);
  }

}
