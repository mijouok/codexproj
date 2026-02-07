package com.nineties.alumni.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
  public static CurrentUser requireCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof CurrentUser cu)) {
      throw new IllegalStateException("No authenticated user");
    }
    return cu;
  }
}
