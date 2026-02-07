package com.nineties.alumni.space.dto;

public class SpaceResponse {
  private long id;
  private String name;
  private String slug;

  public SpaceResponse(long id, String name, String slug) {
    this.id = id;
    this.name = name;
    this.slug = slug;
  }

  public long getId() { return id; }
  public String getName() { return name; }
  public String getSlug() { return slug; }
}
