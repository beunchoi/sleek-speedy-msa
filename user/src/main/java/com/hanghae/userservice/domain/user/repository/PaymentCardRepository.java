package com.hanghae.userservice.domain.user.repository;

import com.hanghae.userservice.domain.user.entity.PaymentCard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
  Optional<PaymentCard> findByCardId(String cardId);
  List<PaymentCard> findAllByUserId(String userId);

}
