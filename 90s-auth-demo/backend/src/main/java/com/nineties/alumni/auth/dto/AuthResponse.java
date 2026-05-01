package com.nineties.alumni.auth.dto;

import java.util.List;

public class AuthResponse {
  private String accessToken;
  private String refreshToken;
  private String userId;
  private int trustLevel;
  private List<String> roles;

  public AuthResponse(String accessToken, String refreshToken, String userId, int trustLevel, List<String> roles) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.userId = userId;
    this.trustLevel = trustLevel;
    this.roles = roles;
  }

  public String getAccessToken() { return accessToken; }
  public String getRefreshToken() { return refreshToken; }
  public String getUserId() { return userId; }
  public int getTrustLevel() { return trustLevel; }
  public List<String> getRoles() { return roles; }
}
