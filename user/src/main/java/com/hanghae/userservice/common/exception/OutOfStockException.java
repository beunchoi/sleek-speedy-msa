package com.hanghae.userservice.common.exception;

public class OutOfStockException extends RuntimeException {

  public OutOfStockException(String message) {
    super(message);
  }
}
