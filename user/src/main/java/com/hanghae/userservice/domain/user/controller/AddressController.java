package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.common.dto.ResponseMessage;
import com.hanghae.userservice.common.util.ParseRequestUtil;
import com.hanghae.userservice.domain.user.dto.address.AddressRequestDto;
import com.hanghae.userservice.domain.user.dto.address.AddressResponseDto;
import com.hanghae.userservice.domain.user.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

  private final AddressService addressService;

  @PostMapping
  public ResponseEntity<ResponseMessage> createAddress(HttpServletRequest request, @RequestBody AddressRequestDto requestDto) {
    String userId = ParseRequestUtil.extractUserIdFromRequest(request);
    AddressResponseDto response = addressService.createAddress(requestDto, userId);

    ResponseMessage message = ResponseMessage.builder()
        .data(response)
        .statusCode(201)
        .resultMessage("주소가 저장되었습니다.")
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @GetMapping
  public ResponseEntity<ResponseMessage> getAddress(HttpServletRequest request) {
    String userId = ParseRequestUtil.extractUserIdFromRequest(request);
    AddressResponseDto response = addressService.getAddress(userId);

    ResponseMessage message = ResponseMessage.builder()
        .data(response)
        .statusCode(200)
        .resultMessage("주소 조회")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

}
