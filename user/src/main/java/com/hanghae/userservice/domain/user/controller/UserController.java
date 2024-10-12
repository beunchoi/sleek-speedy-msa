package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.domain.user.dto.AddressRequestDto;
import com.hanghae.userservice.domain.user.dto.AddressResponseDto;
import com.hanghae.userservice.domain.user.dto.MailAuthDto;
import com.hanghae.userservice.domain.user.dto.MailRequestDto;
import com.hanghae.userservice.domain.user.dto.SignupRequestDto;
import com.hanghae.userservice.domain.user.dto.SignupResponseDto;
import com.hanghae.userservice.domain.user.dto.UserResponseDto;
import com.hanghae.userservice.domain.user.service.MailService;
import com.hanghae.userservice.domain.user.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
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

  private final MailService mailService;
  private final UserService userService;

  /* Send Email: 인증번호 전송 버튼 click */
  @PostMapping("/users/sendEmail")
  public Map<String, String> mailSend(@RequestBody @Valid MailRequestDto mailRequestDto)
      throws MessagingException {
    String code = mailService.sendSimpleMessage(mailRequestDto.getEmail());
    // response를 JSON 문자열으로 반환
    Map<String, String> response = new HashMap<>();
    response.put("code", code);

    return response;
  }

  /* Email Auth: 인증번호 입력 후 인증 버튼 click */
  @PostMapping("/users/authEmail")
  public String authCheck(@RequestBody @Valid MailAuthDto mailAuthDto) {
    Boolean checked = mailService.checkAuthNum(mailAuthDto.getMail(), mailAuthDto.getAuthNum());
    if (checked) {
      return "이메일 인증 성공!";
    }
    else {
      throw new IllegalArgumentException("이메일 인증 실패!");
    }
  }

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
