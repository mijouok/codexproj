package com.nineties.alumni.space.dto;

public class MembershipResponse {
  private String spaceId;
  private String userId;
  private String status;

  public MembershipResponse(String spaceId, String userId, String status) {
    this.spaceId = spaceId;
    this.userId = userId;
    this.status = status;
  }

  public String getSpaceId() { return spaceId; }
  public String getUserId() { return userId; }
  public String getStatus() { return status; }
}
