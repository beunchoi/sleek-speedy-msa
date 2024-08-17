package com.hanghae.sleekspeedy.domain.user.service;

import com.hanghae.sleekspeedy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInternalService {

  private final UserRepository userRepository;


}
