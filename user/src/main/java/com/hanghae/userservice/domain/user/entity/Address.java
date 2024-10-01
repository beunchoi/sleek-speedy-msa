package com.hanghae.userservice.domain.user.entity;

import com.hanghae.userservice.domain.user.dto.AddressRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "address")
public class Address {

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
