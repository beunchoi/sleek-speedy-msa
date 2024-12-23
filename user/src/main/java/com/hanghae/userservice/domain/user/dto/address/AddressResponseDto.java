package com.hanghae.userservice.domain.user.dto.address;

import com.hanghae.userservice.domain.user.entity.Address;
import lombok.Data;

@Data
public class AddressResponseDto {
  private String userId;
  private String addressName;
  private String address;
  private String detailAddress;


  public AddressResponseDto(Address address) {
    this.userId = address.getUserId();
    this.addressName = address.getAddressName();
    this.address = address.getAddress();
    this.detailAddress = address.getDetailAddress();
  }
}
