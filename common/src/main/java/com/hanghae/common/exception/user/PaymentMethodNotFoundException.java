package com.hanghae.common.exception.user;

public class PaymentMethodNotFoundException extends RuntimeException {

  public PaymentMethodNotFoundException(String message) {
    super(message);
  }
}
