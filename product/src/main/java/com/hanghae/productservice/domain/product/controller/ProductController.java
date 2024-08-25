package com.hanghae.productservice.domain.product.controller;

import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-service")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final Environment env;

  @GetMapping("/health_check")
  public String status() {
    return String.format("상품 서비스 정상 작동 중입니다. 포트 번호 : %s", env.getProperty("local.server.port"));
  }

  @PostMapping("/products")
  public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
    ProductResponseDto response = productService.createProduct(requestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/products")
  public ResponseEntity<List<ProductResponseDto>> getProducts() {
    List<ProductResponseDto> response = productService.getProducts();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

//  @GetMapping("/{productId}")
//  public ProductResponseDto getProductDescription(@PathVariable Long productId) {
//    return productService.getProductDescription(productId);
//  }
}
