package com.nineties.alumni.space.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "spaces")
@Getter
@Setter
public class Space {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String slug;

  @Column(nullable = false)
  private Long createdBy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SpaceVisibility visibility = SpaceVisibility.INVITE_ONLY;

  private Instant createdAt = Instant.now();
}
