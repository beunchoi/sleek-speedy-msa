package com.hanghae.userservice.domain.user.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
  private String accessToken;
  private String refreshToken;

}
