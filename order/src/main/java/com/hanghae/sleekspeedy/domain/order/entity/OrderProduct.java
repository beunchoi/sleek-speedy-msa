package com.hanghae.sleekspeedy.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_product")
@NoArgsConstructor
public class OrderProduct {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "product_id", nullable = false)
//  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderProductStatus status;

  @Column
  private LocalDate deliveredDate;

  @Column
  private LocalDate returnRequestedDate;

  @Column(nullable = false)
  private int count;

//  public void cancel() {
//    if (this.status == OrderProductStatus.ORDERED) {
//      this.status = OrderProductStatus.CANCELED;
//      product.incrementStock();
//    } else {
//      throw new IllegalStateException("배송 전의 상품만 취소할 수 있습니다.");
//    }
//  }

  public void orderedToShipped() {
    this.status = OrderProductStatus.SHIPPED;
  }

  public void shippedToDelivered() {
    this.status = OrderProductStatus.DELIVERED;
  }

  public void requestReturn() {
    if (this.status == OrderProductStatus.DELIVERED && LocalDate.now().isBefore(deliveredDate.plusDays(2))) {
      this.status = OrderProductStatus.RETURN_REQUESTED;
      this.returnRequestedDate = LocalDate.now();
    } else {
      throw new IllegalStateException("반품 가능한 기간이 지났거나, 배송 완료 상태가 아닙니다.");
    }
  }

//  public void completeReturn() {
//    if (this.status == OrderProductStatus.RETURN_REQUESTED && LocalDate.now().isAfter(returnRequestedDate.plusDays(1))) {
//      this.status = OrderProductStatus.RETURNED;
//      product.incrementStock();
//    } else {
//      throw new IllegalStateException("반품 처리 조건이 맞지 않습니다.");
//    }
//  }
}
