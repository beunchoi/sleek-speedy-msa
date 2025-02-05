package com.hanghae.userservice.domain.user.dto.paymentmethod;

import lombok.Getter;

@Getter
public class PaymentMethodRequestDto {
  private String paymentMethodType;
  private String bankName;
  private String bankAccountNum;
  private String cardNum;
  private String cardType;
  private String expiryDate;
}
