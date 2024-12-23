package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.domain.user.dto.address.AddressRequestDto;
import com.hanghae.userservice.domain.user.dto.address.AddressResponseDto;
import com.hanghae.userservice.domain.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class AddressController {

  private final AddressService addressService;

  @PostMapping("/address")
  public ResponseEntity<AddressResponseDto> createAddress(@RequestBody AddressRequestDto requestDto, @RequestHeader("X-User-Id") String userId) {
    AddressResponseDto response = addressService.createAddress(requestDto, userId);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/address")
  public ResponseEntity<AddressResponseDto> getAddress(@RequestHeader("X-User-Id") String userId) {
    AddressResponseDto response = addressService.getAddress(userId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
