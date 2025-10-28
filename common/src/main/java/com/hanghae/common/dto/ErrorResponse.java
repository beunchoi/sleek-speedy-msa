package com.hanghae.common.dto;

import lombok.Value;

@Value
public class ErrorResponse {

  private String errorCode;
  private String errorMessage;
}
