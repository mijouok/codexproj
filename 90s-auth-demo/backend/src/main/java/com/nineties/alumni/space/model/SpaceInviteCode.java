package com.nineties.alumni.space.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("space_invite_codes")
@Getter
@Setter
public class SpaceInviteCode {
  @Id
  private String id;

  private String spaceId;

  @Indexed(name = "code", unique = true)
  private String code;

  private InviteCodeType type = InviteCodeType.MULTI_USE;

  private Integer maxUses = 999999;
  private Integer usedCount = 0;

  private Instant expiresAt;

  private String createdBy;

  private Instant createdAt = Instant.now();

  public boolean isUsable() {
    if (expiresAt != null && Instant.now().isAfter(expiresAt)) return false;
    return usedCount < maxUses;
  }
}
