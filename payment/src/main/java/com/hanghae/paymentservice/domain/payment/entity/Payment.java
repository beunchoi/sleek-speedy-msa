package com.hanghae.paymentservice.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String tid;
  @Column
  private String orderId;
  @Column
  private String userId;
  @Column
  private String productId;
  @Column
  private String quantity;
  @Column
  private boolean availability = true;

  public Payment(String tid, String orderId, String userId, String productId, String quantity) {
    this.tid = tid;
    this.orderId = orderId;
    this.userId = userId;
    this.productId = productId;
    this.quantity = quantity;
  }

  public void deletePayment() {
    this.availability = false;
  }
}
