package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.common.dto.ProductResponseDto;
import com.hanghae.userservice.domain.user.client.wish.ProductServiceClient;
import com.hanghae.userservice.domain.user.entity.Wish;
import com.hanghae.userservice.domain.user.repository.WishRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishService {
  private final WishRepository wishRepository;
  private final ProductServiceClient productServiceClient;

  @Transactional
  public Wish createUpdateWish(String userId, String productId) {
    ProductResponseDto response = productServiceClient.getProductByProductId(productId);
    if (response == null) {
      throw new IllegalArgumentException("해당 상품이 존재하지 않습니다.");
    }

    Wish wish = wishRepository.findByUserIdAndProductId(userId, response.getProductId()).orElse(null);

    if (wish == null) {
      return wishRepository.save(new Wish(userId, response.getProductId(), true));
    } else if (!wish.isActive()) {
      wish.activate();
      return wish;
    } else {
      wish.deactivate();
      return wish;
    }
  }

  public List<Wish> getMyWishList(String userId) {
    List<Wish> wishList = wishRepository.findAllByUserIdAndActiveIsTrue(userId);

    return wishList;
  }

}
