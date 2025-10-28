package com.hanghae.userservice.domain.user.entity;

import com.hanghae.common.util.Timestamp;
import com.hanghae.userservice.domain.user.dto.address.AddressRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "address")
public class Address extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String userId;
  @Column(nullable = false)
  private String addressName;
  @Column(nullable = false)
  private String address;
  @Column
  private String detailAddress;

  public Address(AddressRequestDto requestDto, String userId) {
    this.addressName = requestDto.getAddressName();
    this.address = requestDto.getAddress();
    this.detailAddress = requestDto.getDetailAddress();
    this.userId = userId;
  }

}
