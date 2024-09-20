package com.hanghae.paymentservice.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "payment")
@Getter
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
  private String pgToken;

  public Payment(String tid, String orderId, String userId, String productId, String quantity, String pgToken) {
    this.tid = tid;
    this.orderId = orderId;
    this.userId = userId;
    this.productId = productId;
    this.quantity = quantity;
    this.pgToken = pgToken;
  }
}
