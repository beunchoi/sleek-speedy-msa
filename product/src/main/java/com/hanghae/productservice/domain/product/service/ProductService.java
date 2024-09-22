package com.hanghae.productservice.domain.product.service;

import com.hanghae.productservice.domain.product.event.FailedStockUpdateEvent;
import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.event.SaveProductStockEvent;
import com.hanghae.productservice.domain.product.entity.Product;
import com.hanghae.productservice.domain.product.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
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
  private static final String STOCK_KEY_PREFIX = "product:stock:";

  public ProductResponseDto createProduct(ProductRequestDto requestDto) {
    Product product = productRepository.save(new Product(requestDto));

    return new ProductResponseDto(product);
  }

  public List<ProductResponseDto> getProducts() {
    List<Product> products = productRepository.findAll();
    return products.stream().map(ProductResponseDto::new).collect(Collectors.toList());
  }

  public ProductResponseDto getProductByProductId(String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    return new ProductResponseDto(product);
  }

  @Transactional
  public ProductResponseDto incrementProductStock(String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    product.incrementStock();
    return new ProductResponseDto(product);
  }

  public void initializeStock(String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + productId,
        String.valueOf(product.getStock()));
  }

  public String getProductStock(String productId) {

    return redisTemplate.opsForValue().get(STOCK_KEY_PREFIX + productId);
  }

  @Transactional
  public void decreaseProductStock(SaveProductStockEvent event) {
    try {
      Product product = productRepository.findByProductId(event.getProductId())
          .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

      product.updateStock(product.getStock() - event.getQuantity());
    } catch (Exception e) {
      this.rollbackProduct(new FailedStockUpdateEvent(
          event.getProductId(),
          event.getOrderId(),
          event.getQuantity()
      ));
    }
  }

  public void rollbackProduct(FailedStockUpdateEvent event) {
    String stockKey = STOCK_KEY_PREFIX + event.getProductId();

    redisTemplate.opsForValue().increment(stockKey, event.getQuantity());

    rabbitTemplate.convertAndSend(exchangeErr, queueErrPayment, event);
  }
}
