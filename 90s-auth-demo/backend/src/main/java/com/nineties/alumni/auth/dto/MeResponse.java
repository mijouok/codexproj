package com.nineties.alumni.auth.dto;

import java.util.List;

public class MeResponse {
  private long userId;
  private String nickname;
  private String email;
  private String phone;
  private int trustLevel;
  private List<String> roles;

  public MeResponse(long userId, String nickname, String email, String phone, int trustLevel, List<String> roles) {
    this.userId = userId;
    this.nickname = nickname;
    this.email = email;
    this.phone = phone;
    this.trustLevel = trustLevel;
    this.roles = roles;
  }

  public long getUserId() { return userId; }
  public String getNickname() { return nickname; }
  public String getEmail() { return email; }
  public String getPhone() { return phone; }
  public int getTrustLevel() { return trustLevel; }
  public List<String> getRoles() { return roles; }
}
