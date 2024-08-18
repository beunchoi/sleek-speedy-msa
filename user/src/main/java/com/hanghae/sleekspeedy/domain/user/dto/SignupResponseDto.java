package com.hanghae.sleekspeedy.domain.user.dto;

import com.hanghae.sleekspeedy.domain.user.entity.User;
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
