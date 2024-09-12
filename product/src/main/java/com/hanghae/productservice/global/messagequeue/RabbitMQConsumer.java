package com.hanghae.productservice.global.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.productservice.domain.product.entity.Product;
import com.hanghae.productservice.domain.product.repository.ProductRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQConsumer {
  private final ProductRepository productRepository;

  @RabbitListener(queues = "product-queue")
  @Transactional
  public void updateStock(String message) {
    log.info("RabbitMQ Message -> " + message);

    Map<Object, Object> map = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    try {
      map = mapper.readValue(message, new TypeReference<Map<Object, Object>>() {});
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    }

    Product product = productRepository.findByProductId((String) map.get("productId"))
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    product.updateStock(product.getStock() - (Integer) map.get("quantity"));
  }
}
