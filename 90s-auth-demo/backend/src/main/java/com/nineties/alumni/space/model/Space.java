package com.nineties.alumni.space.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("spaces")
@Getter
@Setter
public class Space {
  @Id
  private String id;

  private String name;

  @Indexed(unique = true)
  private String slug;

  private String createdBy;

  private SpaceVisibility visibility = SpaceVisibility.INVITE_ONLY;

  private Instant createdAt = Instant.now();
}
