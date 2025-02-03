package com.hanghae.userservice.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hanghae.userservice.domain.user.entity.User;
import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.Data;

@Data
public class UserResponseDto {
  private String userId;
  private String email;
  private String name;
  private String phoneNum;
  private String profile;
  private UserRoleEnum role;

  public UserResponseDto(User user) {
    this.userId = user.getUserId();
    this.email = user.getEmail();
    this.name = user.getName();
    this.role = user.getRole();
  }

}
