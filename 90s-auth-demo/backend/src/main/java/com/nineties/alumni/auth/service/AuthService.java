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
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class AuthService {
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{6,20}$");

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
    return s != null && EMAIL_PATTERN.matcher(s).matches();
  }

  private boolean isPhone(String s) {
    return s != null && PHONE_PATTERN.matcher(s).matches();
  }

  private String normalizeIdentifier(String identifier) {
    String id = identifier == null ? "" : identifier.trim();
    return id.contains("@") ? id.toLowerCase(Locale.ROOT) : id;
  }

  private void requireValidIdentifier(String id) {
    if (!isEmail(id) && !isPhone(id)) {
      throw new ApiException("INVALID_IDENTIFIER", "Identifier must be a valid email or phone number", HttpStatus.BAD_REQUEST);
    }
  }

  private ApiException invalidCredentials() {
    return new ApiException("INVALID_CREDENTIALS", "Invalid credentials", HttpStatus.UNAUTHORIZED);
  }

  public AuthResponse register(RegisterRequest req) {
    String id = normalizeIdentifier(req.getIdentifier());
    requireValidIdentifier(id);
    User u = new User();
    u.setEmail(isEmail(id) ? id : null);
    u.setPhone(!isEmail(id) ? id : null);
    u.setNickname(req.getNickname().trim());
    u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    u.setStatus(UserStatus.ACTIVE);
    u.setTrustLevel(0);
    u.setCreatedAt(Instant.now());
    u.setUpdatedAt(Instant.now());
    userRepository.save(u);

    // Default role: member (platform scope)
    roleService.assignPlatformRole(u.getId(), RoleNames.MEMBER);

    return issueTokens(u);
  }

  public AuthResponse login(LoginRequest req) {
    String id = normalizeIdentifier(req.getIdentifier());
    if (!isEmail(id) && !isPhone(id)) {
      throw invalidCredentials();
    }
    User u;
    u = isEmail(id)
      ? userRepository.findByEmail(id).orElseThrow(this::invalidCredentials)
      : userRepository.findByPhone(id).orElseThrow(this::invalidCredentials);

    if (u.getStatus() == UserStatus.BANNED) {
      throw new ApiException("BANNED", "User is banned", HttpStatus.FORBIDDEN);
    }
    if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
      throw invalidCredentials();
    }
    u.setUpdatedAt(Instant.now());
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
      if (existing.getRevokedAt() != null) {
        revokeActiveRefreshTokens(existing.getUserId());
      }
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
    rt.setCreatedAt(Instant.now());
    refreshTokenRepository.save(rt);

    return new AuthResponse(access, refresh, u.getId(), u.getTrustLevel(), roles);
  }

  private String newRefreshToken() {
    byte[] bytes = new byte[48];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private void revokeActiveRefreshTokens(String userId) {
    Instant now = Instant.now();
    List<RefreshToken> activeTokens = refreshTokenRepository.findByUserIdAndRevokedAtIsNull(userId);
    for (RefreshToken token : activeTokens) {
      token.setRevokedAt(now);
    }
    refreshTokenRepository.saveAll(activeTokens);
  }

}
