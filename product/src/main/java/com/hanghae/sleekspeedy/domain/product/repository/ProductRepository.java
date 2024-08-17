package com.hanghae.sleekspeedy.domain.product.repository;

import com.hanghae.sleekspeedy.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByProductId(String productId);
}
