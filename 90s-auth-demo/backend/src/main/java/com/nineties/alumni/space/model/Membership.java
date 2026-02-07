package com.nineties.alumni.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "memberships", uniqueConstraints = @UniqueConstraint(columnNames = {"spaceId", "userId"}))
@Getter
@Setter
public class Membership {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long spaceId;

  @Column(nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MembershipStatus membershipStatus = MembershipStatus.ACTIVE;

  private String displayName;

  private Instant joinedAt = Instant.now();
}
