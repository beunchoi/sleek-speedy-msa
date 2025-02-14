package com.hanghae.userservice.domain.user.entity;

import com.hanghae.userservice.common.util.Timestamp;
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
@Table(name = "bank_account")
@NoArgsConstructor
@Getter
public class PaymentBankAccount extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true)
  private String bankAccountId;
  @Column(nullable = false, unique = true)
  private String userId;
  @Column(nullable = false)
  private String bankName;
  @Column(nullable = false, unique = true)
  private String bankAccountNum;
  @Column(nullable = false)
  private boolean active = true;

  public PaymentBankAccount(String bankAccountId, String userId, PaymentMethodRequestDto requestDto) {
    this.bankAccountId = bankAccountId;
    this.userId = userId;
    this.bankName = requestDto.getBankName();
    this.bankAccountNum = requestDto.getBankAccountNum();
  }

  public void deleteBankAccount() {
    this.active = false;
  }

}
