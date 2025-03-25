package com.hanghae.productservice.domain.product.repository;

import com.hanghae.productservice.domain.product.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.productId=:productId")
  Optional<Product> findByProductIdWithLock(@Param("productId") String productId);
  Optional<Product> findByProductId(String productId);
  Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
