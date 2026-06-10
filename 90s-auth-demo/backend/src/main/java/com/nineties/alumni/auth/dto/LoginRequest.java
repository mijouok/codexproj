package com.nineties.alumni.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
  @NotBlank
  @Size(max = 254)
  private String identifier;

  @NotBlank
  @Size(max = 128)
  private String password;

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
}
