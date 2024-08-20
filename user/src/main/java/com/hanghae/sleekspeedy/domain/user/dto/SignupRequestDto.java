package com.hanghae.sleekspeedy.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignupRequestDto {

  @Pattern(regexp = "^[a-z0-9]{4,10}", message = "name은 숫자 및 알파벳 소문자 4~10자로 입력해주세요.")
  private String name;
  @Email(message = "옳바른 email 형식이 아닙니다.")
  private String email;
  @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?]{8,15}", message = "비밀번호는 숫자 및 알파벳 대소문자 그리고 특수문자를 포함한 8~15자로 입력해주세요.")
  private String password;
  @NotNull(message = "전화번호를 입력해주세요")
  private String phoneNum;
  @NotNull(message = "주소를 입력해주세요")
  private String address;
  private String adminToken = "";
}
