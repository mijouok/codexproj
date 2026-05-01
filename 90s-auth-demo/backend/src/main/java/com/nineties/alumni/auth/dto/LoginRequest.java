package com.nineties.alumni.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
  @NotBlank
  private String identifier;
  private String email;
  private String phone;

  @NotBlank
  private String password;

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
