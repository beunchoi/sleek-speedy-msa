package com.hanghae.productservice.domain.product.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.hanghae.productservice.domain.product.dto.PurchaseRequestDto;
import com.hanghae.productservice.domain.product.event.StockCheckEvent;
import com.hanghae.productservice.domain.product.producer.ProductEventProducer;
import com.hanghae.productservice.domain.product.repository.ProductRepository;
import com.hanghae.productservice.domain.product.repository.ProductStockRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  ProductRepository productRepository;
  @Mock
  ProductStockRepository productStockRepository;
  @Mock
  ProductEventProducer productEventProducer;
  @InjectMocks
  ProductServiceImpl productService;

  @Test
  @DisplayName("재고 확인 중 오류")
  void purchaseProduct_1() {
    // given
    String productId = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();
    PurchaseRequestDto requestDto = new PurchaseRequestDto(2, "card");

    when(productStockRepository.checkAndDecreaseStock(productId, requestDto.getQuantity()))
        .thenReturn(null);

    // when
    // then
    assertThatThrownBy(() -> productService.purchaseProduct(productId, userId, requestDto))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("재고 부족")
  void purchaseProduct_2() {
    // given
    String productId = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();
    PurchaseRequestDto requestDto = new PurchaseRequestDto(2, "card");

    when(productStockRepository.checkAndDecreaseStock(productId, requestDto.getQuantity()))
        .thenReturn(-1L);

    // when
    // then
    assertThatThrownBy(() -> productService.purchaseProduct(productId, userId, requestDto))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("가격 조회 중 에러 발생")
  void purchaseProduct_3() {
    // given
    String productId = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();
    PurchaseRequestDto requestDto = new PurchaseRequestDto(2, "card");

    when(productStockRepository.checkAndDecreaseStock(productId, requestDto.getQuantity()))
        .thenReturn(3L);
    when(productStockRepository.getPrice(productId)).thenThrow(IllegalArgumentException.class);
    when(productStockRepository.increaseStock(productId, requestDto.getQuantity())).thenReturn(5L);
    // when
    // then
    assertThatThrownBy(() -> productService.purchaseProduct(productId, userId, requestDto))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("메시지 발행 중 에러 발생")
  void purchaseProduct_4() {
    // given
    String productId = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();
    String price = "10000";
    PurchaseRequestDto requestDto = new PurchaseRequestDto(2, "card");

    when(productStockRepository.checkAndDecreaseStock(productId, requestDto.getQuantity()))
        .thenReturn(3L);
    when(productStockRepository.getPrice(productId)).thenReturn(price);
    doThrow(RuntimeException.class).when(productEventProducer).publish(any(StockCheckEvent.class));
    when(productStockRepository.increaseStock(productId, requestDto.getQuantity())).thenReturn(5L);
    // when
    // then
    assertThatThrownBy(() -> productService.purchaseProduct(productId, userId, requestDto))
        .isInstanceOf(RuntimeException.class);
  }

//  @Test
//  public void decrease_test() {
//    PaymentSuccessEvent event = new PaymentSuccessEvent("1", "123", 1);
//    productService.decreaseProductStock(event);
//
//    Product product = productRepository.findByProductId("1")
//        .orElseThrow(() -> new RuntimeException("asd"));
//
//    assertThat(product.getStock()).isEqualTo(999);
//  }
//
//  @Test
//  void decreaseProductStock() throws InterruptedException {
//    int threadCount = 100000;
//    ExecutorService executorService = Executors.newFixedThreadPool(32);
//    CountDownLatch latch = new CountDownLatch(threadCount);
//
//    for (int i = 0; i < threadCount; i++) {
//      executorService.submit(() -> {
//        try {
//          PaymentSuccessEvent event = new PaymentSuccessEvent(
//              "1", "123", 1);
//          productService.decreaseProductStock(event);
//        } finally {
//          latch.countDown();
//        }
//      });
//    }
//
//    latch.await();
//
//    Product product = productRepository.findByProductId("1").orElseThrow();
//
//    // 100 - (100 * 1) = 0
//    assertThat(product.getStock()).isEqualTo(0);
//  }
}