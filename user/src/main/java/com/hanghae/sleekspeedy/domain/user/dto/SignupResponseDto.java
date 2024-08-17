package com.hanghae.sleekspeedy.domain.user.dto;

import com.hanghae.sleekspeedy.domain.user.entity.User;
import lombok.Data;

@Data
public class SignupResponseDto {
  private String email;
  private String username;
  private String userId;

  public SignupResponseDto(User user) {
    this.email = user.getEmail();
    this.username = user.getUsername();
    this.userId = user.getUserId();
  }
}
