package com.hanghae.userservice.domain.user.client.wish;

import com.hanghae.userservice.common.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
  @GetMapping("/internal/product/{productId}")
  ProductResponseDto getProductByProductId(@PathVariable("productId") String productId);

}
