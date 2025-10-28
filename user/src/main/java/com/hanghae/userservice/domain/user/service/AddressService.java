package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.common.exception.user.AddressNotFoundException;
import com.hanghae.userservice.common.exception.user.UserNotFoundException;
import com.hanghae.userservice.domain.user.dto.address.AddressRequestDto;
import com.hanghae.userservice.domain.user.dto.address.AddressResponseDto;
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

  public AddressResponseDto createAddress(AddressRequestDto requestDto,String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

    Address savedAddress = addressRepository.save(new Address(requestDto, user.getUserId()));

    return new AddressResponseDto(savedAddress);
  }

  public AddressResponseDto getAddress(String userId) {
    Address address = addressRepository.findByUserId(userId)
        .orElseThrow(() -> new AddressNotFoundException("사용자의 주소가 존재하지 않습니다."));

    return new AddressResponseDto(address);
  }

}
