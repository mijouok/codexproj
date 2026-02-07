package com.nineties.alumni.space.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinByCodeRequest {
  @NotBlank
  private String code;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
}
