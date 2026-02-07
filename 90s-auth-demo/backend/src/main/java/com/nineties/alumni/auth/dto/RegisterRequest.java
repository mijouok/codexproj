package com.nineties.alumni.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
  private String identifier;

  @NotBlank
  private String password;

  @NotBlank
  private String nickname;

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
}
