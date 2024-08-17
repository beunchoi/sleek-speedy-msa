package com.hanghae.sleekspeedy.domain.basket.repository;

import com.hanghae.sleekspeedy.domain.basket.entity.BasketProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketProductRepository extends JpaRepository<BasketProduct, Long> {

  List<BasketProduct> findAllByBasketId(Long basketId);
}
