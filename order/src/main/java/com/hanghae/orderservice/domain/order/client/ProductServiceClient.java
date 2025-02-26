package com.hanghae.orderservice.domain.order.client;

import com.hanghae.orderservice.common.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

  @PutMapping("/internal/product/{productId}/incr/{quantity}")
  ProductResponseDto increaseProductStock(
      @PathVariable("productId") String productId,
      @PathVariable("quantity") int quantity);

  @GetMapping("/internal/product/{productId}")
  ProductResponseDto getProductByProductId(@PathVariable("productId") String productId);

}
