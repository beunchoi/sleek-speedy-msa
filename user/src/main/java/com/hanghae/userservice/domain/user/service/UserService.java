package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.common.exception.BizRuntimeException;
import com.hanghae.userservice.domain.user.dto.LoginRequestDto;
import com.hanghae.userservice.domain.user.dto.LoginResponseDto;
import com.hanghae.userservice.domain.user.dto.ProfileRequestDto;
import com.hanghae.userservice.domain.user.dto.SignupRequestDto;
import com.hanghae.userservice.domain.user.dto.UserResponseDto;
import com.hanghae.userservice.domain.user.entity.User;
import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import com.hanghae.userservice.domain.user.jwt.JwtUtil;
import com.hanghae.userservice.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final TokenService tokenService;

  @Value("${admin.token}")
  private String ADMIN_TOKEN;

  public UserResponseDto signup(SignupRequestDto request) {
    String userId = UUID.randomUUID().toString();
    String email = request.getEmail();
    String password = passwordEncoder.encode(request.getPassword());

    if (userRepository.findByEmail(email).isPresent()) {
      throw new BizRuntimeException("중복된 email 입니다.");
    }

    UserRoleEnum role = UserRoleEnum.USER;
    if (ADMIN_TOKEN.equals(request.getAdminToken())) {
      role = UserRoleEnum.ADMIN;
    }

    User savedUser = userRepository.save(new User(userId, request, password, role));

    return new UserResponseDto(savedUser);
  }

  public LoginResponseDto login(LoginRequestDto requestDto) {
    User user = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new BizRuntimeException("존재하지 않는 유저입니다."));

    if (passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      return successfulAuthentication(user);
    } else {
      throw new BizRuntimeException("잘못된 비밀번호입니다.");
    }
  }

  @Transactional
  public String updateProfile(String userId, ProfileRequestDto reqDto) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new BizRuntimeException("존재하지 않는 유저입니다."));

    user.updateProfile(reqDto.getProfile());
    return user.getProfile();
  }

  public UserResponseDto getUserInfoByUserId(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new BizRuntimeException("존재하지 않는 유저입니다."));

    return new UserResponseDto(user);
  }

  private LoginResponseDto successfulAuthentication(User user) {
    String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getRole());
    String refreshToken = jwtUtil.createRefreshToken(user.getUserId(), user.getRole());

    tokenService.saveRefreshToken(user.getUserId(), refreshToken);

    return new LoginResponseDto(accessToken, refreshToken);
  }

}