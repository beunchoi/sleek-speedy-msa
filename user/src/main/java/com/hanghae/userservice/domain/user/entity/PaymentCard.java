package com.hanghae.userservice.domain.user.entity;

import com.hanghae.common.util.Timestamp;
import com.hanghae.userservice.domain.user.dto.paymentmethod.PaymentMethodRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card")
@NoArgsConstructor
@Getter
public class PaymentCard extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String cardId;
  @Column(nullable = false, unique = true)
  private String userId;
  @Column(nullable = false, unique = true)
  private String cardNum;
  @Column(nullable = false)
  private String cardType;
  @Column(nullable = false)
  private String expiryDate;
  @Column(nullable = false)
  private boolean active = true;

  public PaymentCard(String cardId, String userId, PaymentMethodRequestDto requestDto) {
    this.cardId = cardId;
    this.userId = userId;
    this.cardNum = requestDto.getCardNum();
    this.cardType = requestDto.getCardType();
    this.expiryDate = requestDto.getExpiryDate();
  }

  public void deleteCard() {
    this.active = false;
  }

}
