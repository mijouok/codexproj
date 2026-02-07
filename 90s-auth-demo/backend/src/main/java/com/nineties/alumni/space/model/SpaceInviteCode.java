package com.nineties.alumni.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "space_invite_codes")
@Getter
@Setter
public class SpaceInviteCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long spaceId;

  @Column(nullable = false, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InviteCodeType type = InviteCodeType.MULTI_USE;

  private Integer maxUses = 999999;
  private Integer usedCount = 0;

  private Instant expiresAt;

  @Column(nullable = false)
  private Long createdBy;

  private Instant createdAt = Instant.now();

  public boolean isUsable() {
    if (expiresAt != null && Instant.now().isAfter(expiresAt)) return false;
    return usedCount < maxUses;
  }
}
