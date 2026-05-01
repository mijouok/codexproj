package com.nineties.alumni.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("users")
@Getter
@Setter
public class User {
  @Id
  private String id;

  @Indexed(unique = true, sparse = true)
  private String email;

  @Indexed(unique = true, sparse = true)
  private String phone;

  private String passwordHash;

  private String nickname;

  private String avatarUrl;

  private UserStatus status = UserStatus.ACTIVE;

  private int trustLevel = 0;

  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();
  private Instant lastLoginAt;

}
