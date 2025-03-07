package com.hanghae.productservice.domain.product.repository;

import com.hanghae.productservice.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByProductId(String productId);
  Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
