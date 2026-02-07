package com.nineties.alumni.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false, length = 256)
  private String tokenHash;

  private String deviceId;

  @Column(nullable = false)
  private Instant expiresAt;

  private Instant revokedAt;

  private Instant createdAt = Instant.now();

  public boolean isActive() {
    return revokedAt == null && Instant.now().isBefore(expiresAt);
  }
}
