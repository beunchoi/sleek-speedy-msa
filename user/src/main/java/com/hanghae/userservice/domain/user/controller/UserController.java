package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.domain.user.dto.SignupRequestDto;
import com.hanghae.userservice.domain.user.dto.SignupResponseDto;
import com.hanghae.userservice.domain.user.dto.UserResponseDto;
import com.hanghae.userservice.domain.user.dto.address.AddressRequestDto;
import com.hanghae.userservice.domain.user.dto.address.AddressResponseDto;
import com.hanghae.userservice.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class UserController {

  private final UserService userService;

  @PostMapping("/users")
  public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    SignupResponseDto response = userService.signup(requestDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/users/address")
  public ResponseEntity<AddressResponseDto> createAddress(@RequestBody AddressRequestDto requestDto, @RequestHeader("X-User-Id") String userId) {
    AddressResponseDto response = userService.createAddress(requestDto, userId);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/users/userlist")
  public ResponseEntity<List<UserResponseDto>> getAllUsers() {
    List<UserResponseDto> users = userService.getAllUsers();

    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @GetMapping("/users")
  public ResponseEntity<UserResponseDto> getUserByUserId(@RequestHeader("X-User-Id") String userId) {
    UserResponseDto response = userService.getUserByUserId(userId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/users/address")
  public ResponseEntity<AddressResponseDto> getAddress(@RequestHeader("X-User-Id") String userId) {
    AddressResponseDto response = userService.getAddress(userId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
