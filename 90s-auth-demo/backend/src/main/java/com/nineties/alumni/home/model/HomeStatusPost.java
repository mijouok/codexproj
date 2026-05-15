package com.nineties.alumni.home.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("home_status_posts")
@Getter
@Setter
public class HomeStatusPost {
  @Id
  private String id;

  @Indexed(name = "userId")
  private String userId;

  private String content;

  private Instant createdAt = Instant.now();
}
