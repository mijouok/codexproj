package com.nineties.alumni.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("refresh_tokens")
@Getter
@Setter
public class RefreshToken {
  @Id
  private String id;

  @Indexed(name = "userId")
  private String userId;

  @Indexed(name = "tokenHash", unique = true)
  private String tokenHash;

  private String deviceId;

  private Instant expiresAt;

  private Instant revokedAt;

  private Instant createdAt = Instant.now();

  public boolean isActive() {
    return revokedAt == null && Instant.now().isBefore(expiresAt);
  }
}
