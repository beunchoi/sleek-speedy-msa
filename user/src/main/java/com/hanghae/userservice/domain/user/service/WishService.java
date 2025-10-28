package com.hanghae.userservice.domain.user.service;

import com.hanghae.common.dto.ProductResponseDto;
import com.hanghae.common.exception.product.ProductNotFoundException;
import com.hanghae.userservice.domain.user.dto.wish.WishResponseDto;
import com.hanghae.userservice.domain.user.entity.Wish;
import com.hanghae.userservice.domain.user.repository.WishRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishService {

  private final WishRepository wishRepository;
  private final ProductFetchService productFetchService;

  @Transactional
  public WishResponseDto createUpdateWish(String userId, String productId) {
    ProductResponseDto response = productFetchService.getProductByProductId(productId);

    if (response == null || response.getTitle().isEmpty()) {
      throw new ProductNotFoundException("해당 상품이 존재하지 않습니다.");
    }

    Wish wish = wishRepository.findByUserIdAndProductId(userId, response.getProductId())
        .orElse(null);

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

  public List<ProductResponseDto> getMyWishList(String userId) {
    List<Wish> wishList = wishRepository.findAllByUserIdAndActiveIsTrue(userId);
    List<ProductResponseDto> responseDtos = new ArrayList<>();

    for (Wish wish : wishList) {
      ProductResponseDto wishProduct = productFetchService.getProductByProductId(wish.getProductId());
      responseDtos.add(wishProduct);
    }

    return responseDtos;
  }

}
