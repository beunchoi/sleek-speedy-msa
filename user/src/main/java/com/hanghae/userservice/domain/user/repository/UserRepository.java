package com.hanghae.userservice.domain.user.repository;

import com.hanghae.userservice.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);
  Optional<User> findByName(String name);

  Optional<User> findByUserId(String userId);
}
