package com.hanghae.userservice.domain.user.dto.address;

import lombok.Data;

@Data
public class AddressRequestDto {
  private String addressName;
  private String address;
  private String detailAddress;
}
