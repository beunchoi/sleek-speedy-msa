package com.hanghae.sleekspeedy.domain.user.service;

import com.hanghae.sleekspeedy.domain.basket.entity.Basket;
import com.hanghae.sleekspeedy.domain.basket.repository.BasketRepository;
import com.hanghae.sleekspeedy.domain.user.dto.OrderResponse;
import com.hanghae.sleekspeedy.domain.user.dto.SignupRequestDto;
import com.hanghae.sleekspeedy.domain.user.dto.SignupResponseDto;
import com.hanghae.sleekspeedy.domain.user.dto.UserResponseDto;
import com.hanghae.sleekspeedy.domain.user.entity.User;
import com.hanghae.sleekspeedy.domain.user.entity.UserRoleEnum;
import com.hanghae.sleekspeedy.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final BasketRepository basketRepository;

  private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

  public SignupResponseDto signup(SignupRequestDto request) {
    String userId = UUID.randomUUID().toString();

    String name = request.getName();
    String email = request.getEmail();
    String password = passwordEncoder.encode(request.getPassword());

    if (userRepository.findByEmail(email).isPresent()) {
      throw new IllegalArgumentException("중복된 email 입니다.");
    }

    if (userRepository.findByName(name).isPresent()) {
      throw new IllegalArgumentException("중복된 name 입니다.");
    }

    UserRoleEnum role = UserRoleEnum.USER;
    if (ADMIN_TOKEN.equals(request.getAdminToken())) {
      role = UserRoleEnum.ADMIN;
    }

    User user = new User(userId, request, password, role);

    userRepository.save(user);
    basketRepository.save(new Basket(user));

    return new SignupResponseDto(user);
  }

  public UserResponseDto getUserByUserId(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new NullPointerException("존재하지 않는 유저입니다."));

    List<OrderResponse> orders = new ArrayList<>();

    return new UserResponseDto(user);
  }

  public List<UserResponseDto> getUserByAll() {
    List<User> users = userRepository.findAll();

    return users.stream().map(UserResponseDto::new).collect(Collectors.toList());
  }

  public UserResponseDto getUserDetailsByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(
        () -> new NullPointerException("해당 유저가 존재하지 않습니다."));

    return new UserResponseDto(user);
  }
}