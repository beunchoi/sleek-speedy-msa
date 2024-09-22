package com.hanghae.paymentservice.domain.payment.repository;

import com.hanghae.paymentservice.domain.payment.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByOrderId(String orderId);
}
