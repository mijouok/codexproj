package com.nineties.alumni.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_roles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "roleId", "scopeType", "scopeId"})
})
@Getter
@Setter
public class UserRole {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long roleId;

  @Column(nullable = false)
  private String scopeType; // PLATFORM or SPACE

  private Long scopeId; // null for PLATFORM

  private Instant createdAt = Instant.now();
}
