package com.nineties.alumni.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtService {

  private final JwtProperties props;
  private final SecretKey key;

  public JwtService(JwtProperties props) {
    this.props = props;
    byte[] keyBytes = props.getSecret().getBytes();
    this.key = Keys.hmacShaKeyFor(padKey(keyBytes));
  }

  private byte[] padKey(byte[] raw) {
    if (raw.length >= 64) return raw;
    byte[] padded = new byte[64];
    for (int i = 0; i < padded.length; i++) {
      padded[i] = raw[i % raw.length];
    }
    return padded;
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
        .build()
        .parseSignedClaims(jwt)
        .getPayload();
  }
}
