package com.hanghae.userservice.global.security;

import com.hanghae.userservice.domain.user.entity.User;
import com.hanghae.userservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // 2
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

    return new UserDetailsImpl(user);
  }
}
