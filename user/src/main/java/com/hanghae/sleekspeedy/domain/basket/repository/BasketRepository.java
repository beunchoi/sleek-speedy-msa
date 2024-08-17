package com.hanghae.sleekspeedy.domain.basket.repository;

import com.hanghae.sleekspeedy.domain.basket.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {

}
