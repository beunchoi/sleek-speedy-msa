package com.hanghae.orderservice.domain.order.entity;

import com.hanghae.orderservice.common.util.Timestamp;
import com.hanghae.orderservice.domain.order.event.StockCheckEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor
public class Order extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String userId;
  @Column(nullable = false, unique = true)
  private String orderId;
  @Column(nullable = false)
  private String productId;
  @Column(nullable = false)
  private Integer quantity;
  @Column(nullable = false)
  private Integer price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.ORDERED;
  @Column
  private LocalDate deliveredDate;
  @Column
  private LocalDate returnRequestedDate;

  public Order(StockCheckEvent event) {
    this.orderId = event.getOrderId();
    this.userId = event.getUserId();
    this.productId = event.getProductId();
    this.quantity = event.getQuantity();
    this.price = event.getPrice();
  }

  public void failure() {
    this.status = OrderStatus.FAILED;
  }

  public void cancel() {
    this.status = OrderStatus.CANCELED;
  }

  public void orderedToShipped() {
    this.status = OrderStatus.SHIPPED;
  }

  public void shippedToDelivered() {
    this.status = OrderStatus.DELIVERED;
    this.deliveredDate = LocalDate.now();
  }

  public void requestProductReturn() {
    this.status = OrderStatus.RETURN_REQUESTED;
    this.returnRequestedDate = LocalDate.now();
  }

  public void completeProductReturn() {
    this.status = OrderStatus.RETURNED;
  }

}
