package com.hanghae.userservice.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.userservice.domain.user.dto.LoginRequestDto;
import com.hanghae.userservice.domain.user.dto.UserResponseDto;
import com.hanghae.userservice.domain.user.entity.UserRoleEnum;
import com.hanghae.userservice.domain.user.service.UserService;
import com.hanghae.userservice.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인할 때 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final JwtUtil jwtUtil;
  private final UserService userService;
  Environment env;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService, Environment env) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
    this.env = env;
    setFilterProcessesUrl("/user-service/login");
  }

  @Override
  public Authentication attemptAuthentication( // 1
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

      return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(
              requestDto.getEmail(),
              requestDto.getPassword(),
              new ArrayList<>()
          )
      );
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override // 3
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
    String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
    UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

    UserResponseDto userDetails = userService.getUserDetailsByEmail(username);

    String token = jwtUtil.createToken(userDetails.getUserId(), role); // 4

    response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
    response.addHeader("userId", userDetails.getUserId());
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    response.setStatus(401);
  }
}
