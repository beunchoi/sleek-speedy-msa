package com.hanghae.userservice.domain.user.repository;

import com.hanghae.userservice.domain.user.entity.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

  Optional<Wish> findByUserIdAndProductId(String userId, String productId);

  List<Wish> findAllByUserIdAndActiveIsTrue(String userId);
}
