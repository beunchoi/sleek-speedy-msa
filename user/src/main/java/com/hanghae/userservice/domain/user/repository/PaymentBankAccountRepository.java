package com.hanghae.userservice.domain.user.repository;

import com.hanghae.userservice.domain.user.entity.PaymentBankAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentBankAccountRepository extends JpaRepository<PaymentBankAccount, Long> {
  Optional<PaymentBankAccount> findByBankAccountId(String bankAccountId);
  List<PaymentBankAccount> findAllByUserId(String userId);

}
