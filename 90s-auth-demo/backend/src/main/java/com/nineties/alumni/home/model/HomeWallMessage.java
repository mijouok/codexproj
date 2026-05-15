package com.nineties.alumni.home.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("home_wall_messages")
@CompoundIndex(name = "toUserId_createdAt", def = "{'toUserId': 1, 'createdAt': -1}")
@Getter
@Setter
public class HomeWallMessage {
  @Id
  private String id;

  @Indexed(name = "fromUserId")
  private String fromUserId;

  @Indexed(name = "toUserId")
  private String toUserId;

  private String content;

  private Instant createdAt = Instant.now();
}
