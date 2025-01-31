package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.domain.user.dto.address.AddressRequestDto;
import com.hanghae.userservice.domain.user.entity.Address;
import com.hanghae.userservice.domain.user.entity.User;
import com.hanghae.userservice.domain.user.repository.AddressRepository;
import com.hanghae.userservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

  private final AddressRepository addressRepository;
  private final UserRepository userRepository;

  public Address createAddress(AddressRequestDto requestDto,String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    return addressRepository.save(new Address(requestDto, user.getUserId()));
  }

  public Address getAddress(String userId) {
    Address address = addressRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자의 주소가 존재하지 않습니다."));

    return address;
  }

}
