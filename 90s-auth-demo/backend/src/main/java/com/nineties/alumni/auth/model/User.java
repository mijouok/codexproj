package com.nineties.alumni.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String phone;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private String nickname;

  private String avatarUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status = UserStatus.ACTIVE;

  @Column(nullable = false)
  private int trustLevel = 0;

  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();
  private Instant lastLoginAt;

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
