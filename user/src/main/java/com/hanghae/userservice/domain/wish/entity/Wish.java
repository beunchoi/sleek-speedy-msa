package com.hanghae.userservice.domain.wish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "wish")
@NoArgsConstructor
public class Wish {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String userId;
  @Column(nullable = false)
  private String productId;
  @Column
  private boolean active;


  public Wish(String userId, String productId, boolean active) {
    this.userId = userId;
    this.productId = productId;
    this.active = active;
  }

  public void updateToTrue() {
    this.active = true;
  }

  public void updateToFalse() {
    this.active = false;
  }
}