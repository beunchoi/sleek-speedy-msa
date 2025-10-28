package com.hanghae.common.exception.user;

public class TokenMismatchException extends RuntimeException {

  public TokenMismatchException(String message) {
    super(message);
  }
}
