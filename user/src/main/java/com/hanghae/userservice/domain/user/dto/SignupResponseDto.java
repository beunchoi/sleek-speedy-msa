package com.hanghae.userservice.domain.user.dto;

import com.hanghae.userservice.domain.user.entity.User;
import lombok.Data;

@Data
public class SignupResponseDto {
  private String email;
  private String name;
  private String userId;

  public SignupResponseDto(User user) {
    this.email = user.getEmail();
    this.name = user.getName();
    this.userId = user.getUserId();
  }
}
