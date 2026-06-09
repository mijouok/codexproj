package com.nineties.alumni.security;

import com.nineties.alumni.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
  public static CurrentUser requireCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof CurrentUser cu)) {
      throw new ApiException("UNAUTHORIZED", "Authentication is required", HttpStatus.UNAUTHORIZED);
    }
    return cu;
  }
}
