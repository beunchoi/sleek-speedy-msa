package com.hanghae.productservice.domain.product.service;

import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.entity.Product;
import com.hanghae.productservice.domain.product.event.FailedStockUpdateEvent;
import com.hanghae.productservice.domain.product.event.SaveProductStockEvent;
import com.hanghae.productservice.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

  private final ProductRepository productRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final RabbitTemplate rabbitTemplate;

  @Value("${message2.err.exchange}")
  private String exchangeErr;
  @Value("${message2.queue.err.payment}")
  private String queueErrPayment;
  private static final String STOCK_KEY = "product:stock:";

  public ProductResponseDto createProduct(ProductRequestDto requestDto) {
    String productId = UUID.randomUUID().toString();
    Product product = productRepository.save(new Product(productId, requestDto));

    return new ProductResponseDto(product);
  }

  public List<ProductResponseDto> getProducts() {
    List<Product> productList = productRepository.findAll();
    List<ProductResponseDto> responseDtos = new ArrayList<>();

    for (Product product : productList) {
      responseDtos.add(new ProductResponseDto(product));
    }

    return responseDtos;
  }

  public void initializeStock(String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    redisTemplate.opsForValue().set(STOCK_KEY + productId,
        String.valueOf(product.getStock()));
  }

  public String getProductStock(String productId) {

    return redisTemplate.opsForValue().get(STOCK_KEY + productId);
  }

  @Transactional
  public ProductResponseDto increaseProductStock(int quantity, String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    product.increaseStock(quantity);
    return new ProductResponseDto(product);
  }

  public ProductResponseDto getProductByProductId(String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    return new ProductResponseDto(product);
  }

  @Transactional
  public void decreaseProductStock(SaveProductStockEvent event) {
    Product product = productRepository.findByProductId(event.getProductId())
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    if (product.getStock() >= event.getQuantity()) {
      product.updateStock(product.getStock() - event.getQuantity());
      log.info("재고 DB 저장 성공");
    } else {
      log.info("재고 부족");
      this.rollbackProduct(new FailedStockUpdateEvent(
          event.getProductId(),
          event.getOrderId(),
          event.getQuantity()
      ));
    }
  }

  public void rollbackProduct(FailedStockUpdateEvent event) {
    String stockKey = STOCK_KEY+ event.getProductId();

    redisTemplate.opsForValue().increment(stockKey, event.getQuantity());

    rabbitTemplate.convertAndSend(exchangeErr, queueErrPayment, event);
  }

}
