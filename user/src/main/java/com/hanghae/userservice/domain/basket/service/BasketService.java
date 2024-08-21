package com.hanghae.userservice.domain.basket.service;

import com.hanghae.userservice.domain.basket.repository.BasketProductRepository;
import com.hanghae.userservice.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketService {

  private final BasketProductRepository basketProductRepository;
//  private final ProductRepository productRepository;

  public void addProductToBasket(User user, Long productId) {

//    Product product = productRepository.findById(productId).orElseThrow(() -> new NullPointerException("해당 상품이 존재하지 않습니다."));
//    BasketProduct basketProduct = new BasketProduct(user, product);
//
//    basketProductRepository.save(basketProduct);
    throw new UnsupportedOperationException("TODO: Implement the rest of the functionality");
  }

//  public List<BasketProductResponse> getMyBasketProducts(User user) {
//    List<BasketProduct> productList = basketProductRepository.findAllByBasketId(user.getBasket().getId());
//
//    return productList.stream().map(BasketProductResponse::new).collect(Collectors.toList());
//  }
}
