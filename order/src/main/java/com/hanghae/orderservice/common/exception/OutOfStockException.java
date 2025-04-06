package com.hanghae.orderservice.common.exception;

public class OutOfStockException extends RuntimeException {

  public OutOfStockException(String message) {
    super(message);
  }
}
