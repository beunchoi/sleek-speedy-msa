package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.common.dto.ResponseMessage;
import com.hanghae.userservice.common.util.ParseRequestUtil;
import com.hanghae.userservice.domain.user.dto.LoginRequestDto;
import com.hanghae.userservice.domain.user.dto.LoginResponseDto;
import com.hanghae.userservice.domain.user.dto.ProfileRequestDto;
import com.hanghae.userservice.domain.user.dto.SignupRequestDto;
import com.hanghae.userservice.domain.user.entity.User;
import com.hanghae.userservice.domain.user.jwt.JwtUtil;
import com.hanghae.userservice.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<ResponseMessage> signup(@RequestBody SignupRequestDto requestDto) {
    User createdUser = userService.signup(requestDto);

    ResponseMessage message = ResponseMessage.builder()
        .data(createdUser)
        .statusCode(201)
        .resultMessage("회원 가입 성공")
        .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseMessage> login(@RequestBody LoginRequestDto requestDto) {
    LoginResponseDto response = userService.login(requestDto);

    ResponseCookie cookieRefreshToken = ResponseCookie
        .from("Refresh-token", response.getRefreshToken())
        .path("/user-service/refresh-token")
        .maxAge(JwtUtil.REFRESH_TOKEN_TIME / 1000)
//        .httpOnly(true) // 자바스크립트에서 쿠키 접근 차단
//        .secure(true) // Https 연결에서만 쿠키 전송
        .build();

    ResponseMessage message = ResponseMessage.builder()
        .statusCode(200)
        .resultMessage("로그인 성공")
        .build();

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.AUTHORIZATION, response.getAccessToken())
        .header(HttpHeaders.SET_COOKIE, cookieRefreshToken.toString())
        .body(message);
  }

  @PatchMapping("/profile")
  public ResponseEntity<ResponseMessage> updateProfile(HttpServletRequest request, @RequestBody ProfileRequestDto requestDto) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    String updatedProfile = userService.updateProfile(userId, requestDto);

    ResponseMessage message = ResponseMessage.builder()
        .data(updatedProfile)
        .statusCode(200)
        .resultMessage("프로필 업데이트 성공")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

  @GetMapping
  public ResponseEntity<ResponseMessage> getUserInfoByUserId(HttpServletRequest request) {
    String userId = new ParseRequestUtil().extractUserIdFromRequest(request);
    User user = userService.getUserInfoByUserId(userId);

    ResponseMessage message = ResponseMessage.builder()
        .data(user)
        .statusCode(200)
        .resultMessage("사용자 정보 조회")
        .build();

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }

}
