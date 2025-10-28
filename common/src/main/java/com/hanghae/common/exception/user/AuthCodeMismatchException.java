package com.hanghae.common.exception.user;

public class AuthCodeMismatchException extends RuntimeException {

  public AuthCodeMismatchException(String message) {
    super(message);
  }
}
