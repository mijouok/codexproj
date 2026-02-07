package com.nineties.alumni.auth.service;

import com.nineties.alumni.auth.dto.AuthResponse;
import com.nineties.alumni.auth.dto.LoginRequest;
import com.nineties.alumni.auth.dto.RegisterRequest;
import com.nineties.alumni.auth.dto.RefreshRequest;
import com.nineties.alumni.auth.model.RefreshToken;
import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.model.UserStatus;
import com.nineties.alumni.auth.repo.RefreshTokenRepository;
import com.nineties.alumni.auth.repo.UserRepository;
import com.nineties.alumni.common.ApiException;
import com.nineties.alumni.common.CryptoUtil;
import com.nineties.alumni.security.JwtProperties;
import com.nineties.alumni.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final JwtProperties jwtProperties;
  private final RoleService roleService;
  private final SecureRandom random = new SecureRandom();

  public AuthService(UserRepository userRepository,
                     RefreshTokenRepository refreshTokenRepository,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService,
                     JwtProperties jwtProperties,
                     RoleService roleService) {
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.jwtProperties = jwtProperties;
    this.roleService = roleService;
  }

private boolean isEmail(String s) {
  return s != null && s.contains("@");
}

public AuthResponse register(RegisterRequest req) {
   String id = req.getIdentifier().trim();
  //  User user = isEmail(id);
    User u = new User();
    u.setEmail(isEmail(id) ? id : null);
    u.setPhone(!isEmail(id) ? id : null);
    u.setNickname(req.getNickname().trim());
    u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    u.setStatus(UserStatus.ACTIVE);
    u.setTrustLevel(0);
    userRepository.save(u);

    // Default role: member (platform scope)
    roleService.assignPlatformRole(u.getId(), RoleNames.MEMBER);

    return issueTokens(u);
  }

  public AuthResponse login(LoginRequest req) {
    User u;
    String id = req.getIdentifier().trim();
    User user = isEmail(id)
      ? userRepository.findByEmail(id).orElseThrow(new ApiException("BAD_REQUEST", "email is required", HttpStatus.BAD_REQUEST))
      : userRepository.findByPhone(id).orElseThrow(new ApiException("BAD_REQUEST", "phone is required", HttpStatus.BAD_REQUEST));

    if (u.getStatus() == UserStatus.BANNED) {
      throw new ApiException("BANNED", "User is banned", HttpStatus.FORBIDDEN);
    }
    if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
      throw new ApiException("INVALID_CREDENTIALS", "Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
    u.setLastLoginAt(Instant.now());
    userRepository.save(u);
    return issueTokens(u);
  }

  public AuthResponse refresh(RefreshRequest req) {
    String raw = req.getRefreshToken();
    String hash = CryptoUtil.sha256Hex(raw);
    RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
        .orElseThrow(() -> new ApiException("INVALID_REFRESH", "Invalid refresh token", HttpStatus.UNAUTHORIZED));

    if (!existing.isActive()) {
      throw new ApiException("INVALID_REFRESH", "Refresh token expired or revoked", HttpStatus.UNAUTHORIZED);
    }

    // rotate
    existing.setRevokedAt(Instant.now());
    refreshTokenRepository.save(existing);

    User u = userRepository.findById(existing.getUserId())
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

    if (u.getStatus() == UserStatus.BANNED) {
      throw new ApiException("BANNED", "User is banned", HttpStatus.FORBIDDEN);
    }

    return issueTokens(u);
  }

  public void logout(RefreshRequest req) {
    String hash = CryptoUtil.sha256Hex(req.getRefreshToken());
    refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
      rt.setRevokedAt(Instant.now());
      refreshTokenRepository.save(rt);
    });
  }

  private AuthResponse issueTokens(User u) {
    List<String> roles = roleService.listPlatformRoleNames(u.getId());
    String access = jwtService.createAccessToken(u.getId(), u.getTrustLevel(), roles);
    String refresh = newRefreshToken();

    RefreshToken rt = new RefreshToken();
    rt.setUserId(u.getId());
    rt.setTokenHash(CryptoUtil.sha256Hex(refresh));
    rt.setExpiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenDays() * 86400L));
    refreshTokenRepository.save(rt);

    return new AuthResponse(access, refresh, u.getId(), u.getTrustLevel(), roles);
  }

  private String newRefreshToken() {
    byte[] bytes = new byte[48];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String blankToNull(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}
