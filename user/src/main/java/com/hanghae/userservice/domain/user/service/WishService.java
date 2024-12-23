package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.domain.user.client.wish.ProductServiceClient;
import com.hanghae.userservice.domain.user.dto.wish.ProductResponseDto;
import com.hanghae.userservice.domain.user.dto.wish.WishResponseDto;
import com.hanghae.userservice.domain.user.entity.Wish;
import com.hanghae.userservice.domain.user.repository.WishRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishService {
  private final WishRepository wishRepository;
  private final ProductServiceClient productServiceClient;

  @Transactional
  public void createUpdateWish(String userId, String productId) {
    ProductResponseDto response = productServiceClient.getProductByProductId(productId);
    if (response == null) {
      throw new IllegalArgumentException("해당 상품이 존재하지 않습니다.");
    }

    Wish wish = wishRepository.findByUserIdAndProductId(userId, productId).orElse(null);
    if (wish == null) {
      boolean active = true;
      wishRepository.save(new Wish(userId, productId, active));
    } else {
      wish.updateToTrue();
    }
  }

  public List<WishResponseDto> getMyWishList(String userId) {
    List<Wish> wishList = wishRepository.findAllByUserIdAndActiveIsTrue(userId);

    return wishList.stream().map(WishResponseDto::new).collect(Collectors.toList());
  }

  @Transactional
  public void deleteWish(String userId, String productId) {
    Wish wish = wishRepository.findByUserIdAndProductId(userId, productId)
        .orElseThrow(() -> new IllegalArgumentException("위시 리스트에 등록되지 않았습니다."));

    wish.updateToFalse();
  }
}
