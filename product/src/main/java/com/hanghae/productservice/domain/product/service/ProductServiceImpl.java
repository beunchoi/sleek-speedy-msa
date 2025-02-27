package com.hanghae.productservice.domain.product.service;

import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.entity.Product;
import com.hanghae.productservice.domain.product.event.StockUpdateFailedEvent;
import com.hanghae.productservice.domain.product.event.PaymentSuccessEvent;
import com.hanghae.productservice.domain.product.producer.ProductEventProducer;
import com.hanghae.productservice.domain.product.repository.ProductCacheRepository;
import com.hanghae.productservice.domain.product.repository.ProductRepository;
import com.hanghae.productservice.domain.product.repository.ProductStockRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductStockRepository productStockRepository;
  private final ProductEventProducer productEventProducer;
  private final ProductCacheRepository productCacheRepository;

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
    Product product = productRepository.findByProductId(event.getProductId())
        .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
//    if (product.getStock() <= event.getQuantity()) {
    if (product.getStock() >= event.getQuantity()) {
      product.updateStock(product.getStock() - event.getQuantity());
      log.info("재고 DB 저장 성공");
    } else {
      log.info("재고 부족");
      this.rollbackProduct(new StockUpdateFailedEvent(
          event.getProductId(),
          event.getOrderId(),
          event.getQuantity()
      ));
    }
  }

  @Override
  public void rollbackProduct(StockUpdateFailedEvent event) {
    String productId = event.getProductId();
    int quantity = event.getQuantity();

    productStockRepository.increaseStock(productId, quantity);

    productEventProducer.publishFailedEvent(event);
  }

}
