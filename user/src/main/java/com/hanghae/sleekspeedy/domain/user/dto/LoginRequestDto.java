package com.hanghae.sleekspeedy.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDto {
  @NotNull(message = "이메일을 입력해주세요")
  private String email;
  @NotNull(message = "비밀번호를 입력해주세요")
  private String password;
}