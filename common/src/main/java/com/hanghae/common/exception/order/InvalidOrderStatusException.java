package com.hanghae.common.exception.order;

public class InvalidOrderStatusException extends RuntimeException {

  public InvalidOrderStatusException(String message) {
    super(message);
  }
}
