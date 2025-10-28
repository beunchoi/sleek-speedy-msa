package com.hanghae.userservice.domain.user.service;

import com.hanghae.common.exception.user.PaymentMethodNotFoundException;
import com.hanghae.userservice.domain.user.dto.paymentmethod.PaymentMethodRequestDto;
import com.hanghae.userservice.domain.user.dto.paymentmethod.PaymentMethodResponseDto;
import com.hanghae.userservice.domain.user.entity.PaymentBankAccount;
import com.hanghae.userservice.domain.user.entity.PaymentCard;
import com.hanghae.userservice.domain.user.repository.PaymentBankAccountRepository;
import com.hanghae.userservice.domain.user.repository.PaymentCardRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

  private final PaymentCardRepository paymentCardRepository;
  private final PaymentBankAccountRepository paymentBankAccountRepository;

  private static final String CARD = "card";
  private static final String BANK_ACCOUNT = "bankAccount";

  public void createPaymentMethod(String userId, PaymentMethodRequestDto requestDto) {
    String uuid = UUID.randomUUID().toString();

    switch (requestDto.getPaymentMethodType()) {
      case CARD:
        String cardId = CARD + uuid;
        paymentCardRepository.save(new PaymentCard(cardId, userId, requestDto));
        return;

      case BANK_ACCOUNT:
        String bankAccountId = BANK_ACCOUNT + uuid;
        paymentBankAccountRepository.save(new PaymentBankAccount(bankAccountId, userId, requestDto));
        return;

      default:
        throw new IllegalArgumentException("유효하지 않은 결제 수단입니다.");
    }
  }

  public List<PaymentMethodResponseDto> getMyAllPaymentMethod(String userId) {
    List<PaymentCard> cardList = paymentCardRepository.findAllByUserId(userId);
    List<PaymentBankAccount> accountList = paymentBankAccountRepository.findAllByUserId(userId);
    List<PaymentMethodResponseDto> responseList = new ArrayList<>();

    for (PaymentCard card : cardList) {
      responseList.add(new PaymentMethodResponseDto(card));
    }

    for (PaymentBankAccount account : accountList) {
      responseList.add(new PaymentMethodResponseDto(account));
    }

    return responseList;
  }

  @Transactional
  public void deletePaymentMethod(String paymentMethodId) {
    if (paymentMethodId.contains(CARD)) {
      PaymentCard card = paymentCardRepository.findByCardId(paymentMethodId)
          .orElseThrow(() -> new PaymentMethodNotFoundException("해당 결제 수단을 찾을 수 없습니다."));
      card.deleteCard();
    } else if (paymentMethodId.contains(BANK_ACCOUNT)) {
      PaymentBankAccount bankAccount = paymentBankAccountRepository
          .findByBankAccountId(paymentMethodId)
          .orElseThrow(() -> new PaymentMethodNotFoundException("해당 결제 수단을 찾을 수 없습니다."));
      bankAccount.deleteBankAccount();
    } else {
      throw new IllegalArgumentException("유효하지 않은 결제 수단입니다.");
    }
  }

}
