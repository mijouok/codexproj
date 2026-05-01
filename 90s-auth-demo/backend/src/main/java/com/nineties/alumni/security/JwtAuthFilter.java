package com.nineties.alumni.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring("Bearer ".length()).trim();
    try {
      Map<String, Object> claims = jwtService.parseClaims(token);
      Object sub = claims.get("sub");
      if (sub == null) throw new IllegalArgumentException("missing sub");
      String userId = sub.toString();

      int tl = 0;
      Object tlObj = claims.get("tl");
      if (tlObj instanceof Number n) tl = n.intValue();
      else if (tlObj != null) tl = Integer.parseInt(tlObj.toString());

      @SuppressWarnings("unchecked")
      List<String> roles = (List<String>) claims.getOrDefault("roles", List.of());

      List<SimpleGrantedAuthority> authorities = new ArrayList<>();
      for (String r : roles) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
      }

      CurrentUser principal = new CurrentUser(userId, tl, roles);
      Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(auth);
    } catch (Exception ignore) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}
