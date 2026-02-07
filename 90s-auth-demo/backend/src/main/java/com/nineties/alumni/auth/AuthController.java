package com.nineties.alumni.auth;

import com.nineties.alumni.auth.dto.*;
import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.repo.UserRepository;
import com.nineties.alumni.auth.service.AuthService;
import com.nineties.alumni.auth.service.RoleService;
import com.nineties.alumni.security.CurrentUser;
import com.nineties.alumni.security.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;
  private final RoleService roleService;

  public AuthController(AuthService authService, UserRepository userRepository, RoleService roleService) {
    this.authService = authService;
    this.userRepository = userRepository;
    this.roleService = roleService;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/refresh")
  public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
    return authService.refresh(request);
  }

  @PostMapping("/logout")
  public void logout(@Valid @RequestBody RefreshRequest request) {
    authService.logout(request);
  }

  @GetMapping("/me")
  public MeResponse me() {
    CurrentUser cu = SecurityUtil.requireCurrentUser();
    User u = userRepository.findById(cu.userId()).orElseThrow();
    List<String> platformRoles = roleService.listPlatformRoleNames(u.getId());
    return new MeResponse(u.getId(), u.getNickname(), u.getEmail(), u.getPhone(), u.getTrustLevel(), platformRoles);
  }
}
