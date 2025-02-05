package com.hanghae.userservice.domain.user.dto.paymentmethod;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hanghae.userservice.domain.user.entity.PaymentBankAccount;
import com.hanghae.userservice.domain.user.entity.PaymentCard;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(Include.NON_NULL)
public class PaymentMethodResponseDto {
  private String paymentMethodId;
  private String bankName;
  private String bankAccountNum;
  private String cardNum;
  private String cardType;
  private String expiryDate;

  public PaymentMethodResponseDto(PaymentCard card) {
    this.paymentMethodId = card.getCardId();
    this.cardNum = card.getCardNum();
    this.cardType = card.getCardType();
    this.expiryDate = card.getExpiryDate();
  }

  public PaymentMethodResponseDto(PaymentBankAccount account) {
    this.paymentMethodId = account.getBankAccountId();
    this.bankName = account.getBankName();
    this.bankAccountNum = account.getBankAccountNum();
  }

}
