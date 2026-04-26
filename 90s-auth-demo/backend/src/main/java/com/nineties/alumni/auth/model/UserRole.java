package com.nineties.alumni.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("user_roles")
@CompoundIndex(name = "uk_user_roles_assignment", def = "{'userId': 1, 'roleId': 1, 'scopeType': 1, 'scopeId': 1}", unique = true)
@Getter
@Setter
public class UserRole {
  @Id
  private String id;

  private String userId;

  private String roleId;

  private String scopeType; // PLATFORM or SPACE

  private String scopeId; // null for PLATFORM

  private Instant createdAt = Instant.now();
}
