package com.nineties.alumni.space.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateInviteCodeRequest {
  @NotBlank
  private String type; // SINGLE_USE or MULTI_USE

  @Min(1)
  private Integer maxUses;

  @Min(1)
  private Integer expiresInDays;

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Integer getMaxUses() { return maxUses; }
  public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }
  public Integer getExpiresInDays() { return expiresInDays; }
  public void setExpiresInDays(Integer expiresInDays) { this.expiresInDays = expiresInDays; }
}
