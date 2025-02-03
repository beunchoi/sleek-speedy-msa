package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.common.dto.ProductResponseDto;
import com.hanghae.userservice.domain.user.client.wish.ProductServiceClient;
import com.hanghae.userservice.domain.user.dto.wish.WishResponseDto;
import com.hanghae.userservice.domain.user.entity.Wish;
import com.hanghae.userservice.domain.user.repository.WishRepository;
import java.util.ArrayList;
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
  public WishResponseDto createUpdateWish(String userId, String productId) {
    ProductResponseDto response = productServiceClient.getProductByProductId(productId);
    if (response == null) {
      throw new IllegalArgumentException("해당 상품이 존재하지 않습니다.");
    }

    Wish wish = wishRepository.findByUserIdAndProductId(userId, response.getProductId()).orElse(null);

    if (wish == null) {
      Wish savedWish = wishRepository.save(new Wish(userId, response.getProductId(), true));
      return new WishResponseDto(savedWish);
    } else if (!wish.isActive()) {
      wish.activate();
      return new WishResponseDto(wish);
    } else {
      wish.deactivate();
      return new WishResponseDto(wish);
    }
  }

  public List<WishResponseDto> getMyWishList(String userId) {
    List<Wish> wishList = wishRepository.findAllByUserIdAndActiveIsTrue(userId);
    List<WishResponseDto> responseDtos = new ArrayList<>();

    for (Wish wish : wishList) {
      responseDtos.add(new WishResponseDto(wish));
    }

    return responseDtos;
  }

}
