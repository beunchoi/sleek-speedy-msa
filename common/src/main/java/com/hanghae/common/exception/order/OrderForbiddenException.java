package com.hanghae.common.exception.order;

public class OrderForbiddenException extends RuntimeException {

  public OrderForbiddenException(String message) {
    super(message);
  }
}
