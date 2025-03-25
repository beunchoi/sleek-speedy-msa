package com.hanghae.productservice.domain.product.controller;

import com.hanghae.productservice.common.dto.ResponseMessage;
import com.hanghae.productservice.common.util.ParseRequestUtil;
import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import com.hanghae.productservice.domain.product.dto.PurchaseRequestDto;
import com.hanghae.productservice.domain.product.dto.PurchaseResponseDto;
import com.hanghae.productservice.domain.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping("/{productId}/purchase")
  public ResponseEntity<ResponseMessage> purchaseProduct(HttpServletRequest request,
      @PathVariable("productId") String productId,
      @RequestBody PurchaseRequestDto requestDto) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    PurchaseResponseDto response = productService.purchaseProduct(productId, userId, requestDto);

    ResponseMessage message = ResponseMessage.builder()
        .data(response)
        .statusCode(200)
        .resultMessage("주문을 정상적으로 완료하였습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @PostMapping
  public ResponseEntity<ResponseMessage> createProduct(HttpServletRequest request,
      @RequestBody ProductRequestDto requestDto) {
    // 관리자 권한 검증
    ParseRequestUtil.validateAdminRoleFromRequest(request);
    ProductResponseDto response = productService.createProduct(requestDto);

    ResponseMessage message = ResponseMessage.builder()
        .data(response)
        .statusCode(201)
        .resultMessage("상품을 저장했습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponseDto>> getProducts(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size
  ) {
    List<ProductResponseDto> responses = productService.getProducts(page, size);

    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @PostMapping("/{productId}/initialize")
  public ResponseEntity<String> initializeStock(HttpServletRequest request,
      @PathVariable("productId") String productId) {
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

}
