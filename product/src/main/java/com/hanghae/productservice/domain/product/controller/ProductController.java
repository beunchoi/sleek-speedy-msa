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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-service")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping("/{productId}/initialize")
  public ResponseEntity<String> initializeStock(@PathVariable String productId) {
    productService.initializeStock(productId);
    return ResponseEntity.status(HttpStatus.OK).body("재고 초기화 성공");
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

  @GetMapping("/{productId}")
  public ResponseEntity<ProductResponseDto> getProductByProductId(@PathVariable String productId) {
    ProductResponseDto response = productService.getProductByProductId(productId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{productId}/stock")
  public ResponseEntity<String> getProductStock(@PathVariable String productId) {
    String productStock = productService.getProductStock(productId);
    return ResponseEntity.status(HttpStatus.OK).body(productStock);
  }
  @PutMapping("/{productId}/increment")
  public ResponseEntity<ProductResponseDto> incrementProductStock(@PathVariable String productId) {
    ProductResponseDto response = productService.incrementProductStock(productId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
