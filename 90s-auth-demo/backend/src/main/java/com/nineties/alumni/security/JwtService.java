package com.nineties.alumni.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtService {

  private final JwtProperties props;
  private final SecretKey key;

  public JwtService(JwtProperties props) {
    this.props = props;
    if (props.getIssuer() == null || props.getIssuer().isBlank()) {
      throw new IllegalStateException("app.jwt.issuer must be configured");
    }
    if (props.getSecret() == null || props.getSecret().isBlank()) {
      throw new IllegalStateException("JWT_SECRET must be configured");
    }
    byte[] keyBytes = props.getSecret().getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 64) {
      throw new IllegalStateException("JWT_SECRET must be at least 64 bytes for HS512");
    }
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createAccessToken(String userId, int trustLevel, List<String> roles) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(props.getAccessTokenMinutes() * 60L);
    return Jwts.builder()
        .issuer(props.getIssuer())
        .subject(String.valueOf(userId))
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claim("tl", trustLevel)
        .claim("roles", roles)
        .signWith(key, Jwts.SIG.HS512)
        .compact();
  }

  public Map<String, Object> parseClaims(String jwt) {
    return Jwts.parser()
        .verifyWith(key)
        .requireIssuer(props.getIssuer())
        .build()
        .parseSignedClaims(jwt)
        .getPayload();
  }
}
