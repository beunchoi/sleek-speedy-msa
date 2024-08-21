package com.hanghae.userservice.domain.user.service;

import com.hanghae.userservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInternalService {

  private final UserRepository userRepository;


}
