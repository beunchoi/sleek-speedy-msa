package com.hanghae.userservice.domain.user.repository;

import com.hanghae.userservice.domain.user.entity.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

  Optional<Address> findByUserId(String userId);
}
