package com.hanghae.productservice.domain.product.service;

import com.hanghae.productservice.domain.product.entity.Product;
import com.hanghae.productservice.domain.product.repository.ProductRepository;
import com.hanghae.productservice.domain.product.dto.ProductRequestDto;
import com.hanghae.productservice.domain.product.dto.ProductResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public ProductResponseDto createProduct(ProductRequestDto requestDto) {
    Product product = productRepository.save(new Product(requestDto));

    return new ProductResponseDto(product);
  }

  public List<ProductResponseDto> getProducts() {
    List<Product> products = productRepository.findAll();
    return products.stream().map(ProductResponseDto::new).collect(Collectors.toList());
  }

  public ProductResponseDto getProductDescription(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NullPointerException("해당 상품이 존재하지 않습니다."));

    String description = product.getDescription();
    return new ProductResponseDto(description);
  }
}
