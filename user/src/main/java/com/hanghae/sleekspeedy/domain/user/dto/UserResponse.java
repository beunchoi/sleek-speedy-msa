package com.hanghae.sleekspeedy.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hanghae.sleekspeedy.domain.user.entity.User;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class UserResponse {
  private String email;
  private String username;
  private String userId;

  private List<OrderResponse> orders;

  public UserResponse(User user) {
    this.email = user.getEmail();
    this.username = user.getUsername();
    this.userId = user.getUserId();
  }
}
