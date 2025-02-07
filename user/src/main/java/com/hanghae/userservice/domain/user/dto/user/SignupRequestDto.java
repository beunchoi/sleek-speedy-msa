package com.hanghae.userservice.domain.user.dto.user;

import lombok.Getter;

@Getter
public class SignupRequestDto {
  private String name;
  private String email;
  private String password;
  private String phoneNum;
  private String adminToken;

}
