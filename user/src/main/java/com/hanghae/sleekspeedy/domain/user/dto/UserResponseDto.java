package com.hanghae.sleekspeedy.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hanghae.sleekspeedy.domain.user.entity.User;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class UserResponseDto {
  private String email;
  private String name;
  private String userId;

  private List<OrderResponse> orders;

  public UserResponseDto(User user) {
    this.email = user.getEmail();
    this.name = user.getName();
    this.userId = user.getUserId();
  }
}
