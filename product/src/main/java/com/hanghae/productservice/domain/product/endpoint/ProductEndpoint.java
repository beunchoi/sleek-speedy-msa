package com.hanghae.productservice.domain.product.endpoint;

import com.hanghae.productservice.domain.product.event.SaveProductStockEvent;
import com.hanghae.productservice.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProductEndpoint {
  private final ProductService productService;

  @RabbitListener(queues = "${message.queue.product}")
  public void handleProductQueue(SaveProductStockEvent event) {
    productService.decreaseProductStock(event);
  }
}
