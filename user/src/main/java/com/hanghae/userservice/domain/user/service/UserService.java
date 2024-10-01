package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.domain.user.client.OrderServiceClient;
import com.hanghae.userservice.domain.user.dto.AddressRequestDto;
import com.hanghae.userservice.domain.user.dto.AddressResponseDto;
import com.hanghae.userservice.domain.user.dto.OrderResponseDto;
import com.hanghae.userservice.domain.user.dto.SignupRequestDto;
import com.hanghae.userservice.domain.user.dto.SignupResponseDto;
import com.hanghae.userservice.domain.user.dto.UserResponseDto;
import com.hanghae.userservice.domain.user.entity.Address;
import com.hanghae.userservice.domain.user.entity.User;
import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import com.hanghae.userservice.domain.user.repository.AddressRepository;
import com.hanghae.userservice.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final OrderServiceClient orderServiceClient;
  private final CircuitBreakerFactory circuitBreakerFactory;

  @Value("${admin.token}")
  private String ADMIN_TOKEN;

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

    return new SignupResponseDto(user);
  }

  public AddressResponseDto createAddress(AddressRequestDto requestDto,String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    Address address = addressRepository.save(new Address(requestDto, user.getUserId()));

    return new AddressResponseDto(address);
  }

  public UserResponseDto getUserByUserId(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

//    String orderUrl = "http://ORDER-SERVICE/order-service/%s/orders";
//    ResponseEntity<List<OrderResponseDto>> responseEntity = restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//        new ParameterizedTypeReference<List<OrderResponseDto>>() {
//    });
//
//    List<OrderResponseDto> response = responseEntity.getBody();

//    List<OrderResponseDto> response = null;
//    try {
//      response = orderServiceClient.getOrdersByUserId(userId);
//    } catch (FeignException ex) {
//      log.error(ex.getMessage());
//    }

//    List<OrderResponseDto> response = orderServiceClient.getOrdersByUserId(userId);

    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
    List<OrderResponseDto> response = circuitBreaker.run(() -> orderServiceClient.getOrdersByUserId(userId),
        throwable -> new ArrayList<>());

    return new UserResponseDto(user, response);
  }

  public List<UserResponseDto> getAllUsers() {
    List<User> users = userRepository.findAll();

    return users.stream().map(UserResponseDto::new).collect(Collectors.toList());
  }

  public UserResponseDto getUserDetailsByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(
        () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

    return new UserResponseDto(user);
  }

  public UserRoleEnum getUserRole(String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

    return user.getRole();
  }

  public AddressResponseDto getAddress(String userId) {
    Address address = addressRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자의 주소가 존재하지 않습니다."));

    return new AddressResponseDto(address);
  }
}