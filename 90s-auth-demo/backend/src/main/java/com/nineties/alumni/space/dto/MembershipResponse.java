package com.nineties.alumni.space.dto;

public class MembershipResponse {
  private long spaceId;
  private long userId;
  private String status;

  public MembershipResponse(long spaceId, long userId, String status) {
    this.spaceId = spaceId;
    this.userId = userId;
    this.status = status;
  }

  public long getSpaceId() { return spaceId; }
  public long getUserId() { return userId; }
  public String getStatus() { return status; }
}
