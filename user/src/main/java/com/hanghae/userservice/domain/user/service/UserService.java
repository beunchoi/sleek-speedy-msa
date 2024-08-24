package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.domain.basket.entity.Basket;
import com.hanghae.userservice.domain.basket.repository.BasketRepository;
import com.hanghae.userservice.domain.user.client.OrderServiceClient;
import com.hanghae.userservice.domain.user.dto.OrderResponseDto;
import com.hanghae.userservice.domain.user.dto.SignupRequestDto;
import com.hanghae.userservice.domain.user.dto.SignupResponseDto;
import com.hanghae.userservice.domain.user.dto.UserResponseDto;
import com.hanghae.userservice.domain.user.entity.User;
import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import com.hanghae.userservice.domain.user.repository.UserRepository;
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
  private final OrderServiceClient orderServiceClient;

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

//    String orderUrl = "http://ORDER-SERVICE/order-service/%s/orders";
//    ResponseEntity<List<OrderResponseDto>> responseEntity = restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//        new ParameterizedTypeReference<List<OrderResponseDto>>() {
//    });
//
//    List<OrderResponseDto> response = responseEntity.getBody();

    List<OrderResponseDto> response = orderServiceClient.getOrdersByUserId(userId);

    return new UserResponseDto(user, response);
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