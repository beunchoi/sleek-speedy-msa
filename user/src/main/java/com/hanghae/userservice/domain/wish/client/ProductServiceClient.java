package com.hanghae.userservice.domain.wish.client;

import com.hanghae.userservice.domain.wish.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
  @GetMapping("/product-service/{productId}")
  ProductResponseDto getProductByProductId(@PathVariable String productId);
}
