package com.hanghae.orderservice.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoPaymentResponse {
  private String nextRedirectPcUrl;
  private String orderId;
}
