package com.nineties.alumni.space.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("memberships")
@CompoundIndex(name = "uk_membership_space_user", def = "{'spaceId': 1, 'userId': 1}", unique = true)
@Getter
@Setter
public class Membership {
  @Id
  private String id;

  private String spaceId;

  private String userId;

  private MembershipStatus membershipStatus = MembershipStatus.ACTIVE;

  private String displayName;

  private Instant joinedAt = Instant.now();
}
