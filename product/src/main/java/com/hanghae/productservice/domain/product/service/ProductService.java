package com.hanghae.productservice.domain.product.service;

import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.event.PaymentSuccessEvent;
import com.hanghae.productservice.domain.product.event.StockUpdateFailedEvent;
import java.util.List;

public interface ProductService {

  ProductResponseDto createProduct(ProductRequestDto requestDto);
  List<ProductResponseDto> getProducts(int page, int size);
  void initializeStock(String productId);
  String getProductStock(String productId);
  ProductResponseDto increaseProductStock(int quantity, String productId);
  ProductResponseDto getProductByProductId(String productId);
  void decreaseProductStock(PaymentSuccessEvent event);
  void rollbackProduct(StockUpdateFailedEvent event);

}
