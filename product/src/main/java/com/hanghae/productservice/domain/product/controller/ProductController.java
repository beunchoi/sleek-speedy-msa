package com.hanghae.productservice.domain.product.controller;

import com.hanghae.productservice.common.dto.ResponseMessage;
import com.hanghae.productservice.common.util.ParseRequestUtil;
import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.entity.Product;
import com.hanghae.productservice.domain.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ResponseMessage> createProduct(HttpServletRequest request,
      @RequestBody ProductRequestDto requestDto) {
    // 관리자 권한 검증
    ParseRequestUtil.validateAdminRoleFromRequest(request);
    Product savedProduct = productService.createProduct(requestDto);

    ResponseMessage message = ResponseMessage.builder()
        .data(savedProduct)
        .statusCode(201)
        .resultMessage("상품을 저장했습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @GetMapping
  public ResponseEntity<ResponseMessage> getProducts() {
    List<Product> productList = productService.getProducts();

    ResponseMessage message = ResponseMessage.builder()
        .data(productList)
        .statusCode(200)
        .resultMessage("상품 목록 조회")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @PostMapping("/{productId}/initialize")
  public ResponseEntity<String> initializeStock(HttpServletRequest request,
      @PathVariable String productId) {
    // 관리자 권한 검증
    ParseRequestUtil.validateAdminRoleFromRequest(request);
    productService.initializeStock(productId);

    return ResponseEntity.status(HttpStatus.OK).body("재고 초기화 성공");
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

  @GetMapping("/{productId}")
  public ResponseEntity<ProductResponseDto> getProductByProductId(@PathVariable("productId") String productId) {
    ProductResponseDto response = productService.getProductByProductId(productId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
