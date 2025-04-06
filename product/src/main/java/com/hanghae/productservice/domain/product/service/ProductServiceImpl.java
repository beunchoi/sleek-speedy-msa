package com.hanghae.productservice.domain.product.service;

import com.hanghae.productservice.common.exception.OutOfStockException;
import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.dto.PurchaseRequestDto;
import com.hanghae.productservice.domain.product.dto.PurchaseResponseDto;
import com.hanghae.productservice.domain.product.entity.Product;
import com.hanghae.productservice.domain.product.event.OrderFailedEvent;
import com.hanghae.productservice.domain.product.event.StockCheckEvent;
import com.hanghae.productservice.domain.product.event.StockUpdateFailedEvent;
import com.hanghae.productservice.domain.product.event.PaymentSuccessEvent;
import com.hanghae.productservice.domain.product.producer.ProductEventProducer;
import com.hanghae.productservice.domain.product.repository.ProductCacheRepository;
import com.hanghae.productservice.domain.product.repository.ProductRepository;
import com.hanghae.productservice.domain.product.repository.ProductStockRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductStockRepository productStockRepository;
  private final ProductEventProducer productEventProducer;
  private final ProductCacheRepository productCacheRepository;

  @Override
  public PurchaseResponseDto purchaseProduct(String productId, String userId,
      PurchaseRequestDto requestDto) {
    // 구매 가능 시간 체크
      Long result = productStockRepository.checkAndDecreaseStock(productId, requestDto.getQuantity());

      if (result == null || result == -2) {
        throw new IllegalStateException("재고 확인 중 오류가 발생했습니다.");
      } else if (result == -1) {
        throw new IllegalStateException("재고가 부족합니다.");
      }

    try {
      String price = productStockRepository.getPrice(productId);

      String orderId = UUID.randomUUID().toString();
      productEventProducer.publish(new StockCheckEvent(productId, Integer.parseInt(price), userId,
          orderId, requestDto.getQuantity(), requestDto.getPaymentMethodId()));
      return new PurchaseResponseDto(orderId);
    } catch (Exception e) {
      Long restoredStock = productStockRepository.increaseStock(productId, requestDto.getQuantity());
      log.error("재고 확인 후 프로세스 중 에러 발생, 복구된 재고={}, message={}",
          restoredStock, e.getMessage());
      throw e;
    }
  }

  @Override
  public ProductResponseDto createProduct(ProductRequestDto requestDto) {
    Product product = productRepository.save(new Product(requestDto));

    productCacheRepository.deleteCachePage();

    return new ProductResponseDto(product);
  }

  @Cacheable(cacheNames = "getProducts", key = "'product:page:' + #page + ':size:' + #size",
      cacheManager = "productCacheManager")
  @Override
  public List<ProductResponseDto> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Product> pageOfProducts = productRepository.findAllByOrderByCreatedAtDesc(
        pageable);
    List<ProductResponseDto> responseDtos = new ArrayList<>();

    for (Product product : pageOfProducts) {
      responseDtos.add(new ProductResponseDto(product));
    }

    return responseDtos;
  }

  @Override
  public void initializeStock(String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    productStockRepository.initializeStock(productId, product);
  }

  @Override
  public String getProductStock(String productId) {

    return productStockRepository.getProductStock(productId);
  }

  @Transactional
  @Override
  public ProductResponseDto increaseProductStock(int quantity, String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    product.increaseStock(quantity);
    return new ProductResponseDto(product);
  }

  @Override
  public ProductResponseDto getProductByProductId(String productId) {
    Product product = productRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

    return new ProductResponseDto(product);
  }

  @Transactional
  @Override
  public void decreaseProductStock(PaymentSuccessEvent event) {
    try {
      Product product = productRepository.findByProductIdWithLock(event.getProductId())
          .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

      if (product.getStock() < event.getQuantity()) {
        throw new OutOfStockException("재고가 부족합니다.");
      }

      product.updateStock(product.getStock() - event.getQuantity());
      log.info("재고 DB 저장 성공");
    } catch (Exception e) {
      productEventProducer.publishFailedEvent(new StockUpdateFailedEvent(
          event.getProductId(), event.getOrderId(), event.getQuantity()));
      log.error("재고 저장 중 에러 발생", e);
    }
  }

  @Override
  public void restoreRedisStock(OrderFailedEvent event) {
    productStockRepository.increaseStock(event.getProductId(), event.getQuantity());
  }

}
