package com.hanghae.userservice.domain.basket.repository;

import com.hanghae.userservice.domain.basket.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {

}
