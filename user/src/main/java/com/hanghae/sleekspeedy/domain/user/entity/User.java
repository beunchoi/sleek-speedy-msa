package com.hanghae.sleekspeedy.domain.user.entity;

import com.hanghae.sleekspeedy.domain.basket.entity.Basket;
import com.hanghae.sleekspeedy.domain.user.dto.SignupRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Data
@NoArgsConstructor
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String userId;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String phoneNum;

  @Column(nullable = false, unique = true)
  private String address;

  @Column(nullable = false)
  private String profile = "자기소개 해주세요";

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private UserRoleEnum role;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
  private Basket basket;

  public User(String userId, SignupRequestDto request, String password, UserRoleEnum role) {
    this.userId = userId;
    this.username = request.getUsername();
    this.password = password;
    this.email = request.getEmail();
    this.phoneNum = request.getPhoneNum();
    this.address = request.getAddress();
    this.role = role;
  }
}