package com.hanghae.paymentservice.domain.payment.entity;

import com.hanghae.paymentservice.domain.payment.event.OrderCreatedEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
public class Payment extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String paymentId = UUID.randomUUID().toString();
  @Column(nullable = false, unique = true)
  private String orderId;
  @Column(nullable = false)
  private Integer totalPrice;
  @Column(nullable = false)
  private String paymentMethodId;
  @Column
  private boolean active = true;

  public Payment(OrderCreatedEvent event) {
    this.orderId = event.getOrderId();
    this.totalPrice = event.getTotalPrice();
    this.paymentMethodId = event.getPaymentMethodId();
  }

  public void deletePayment() {
    this.active = false;
  }

}
