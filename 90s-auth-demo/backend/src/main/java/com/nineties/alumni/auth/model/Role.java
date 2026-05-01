package com.nineties.alumni.auth.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("roles")
@Getter
@Setter
public class Role {
  @Id
  private String id;

  @Indexed(unique = true)
  private String name;
}
