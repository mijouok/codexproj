package com.nineties.alumni.admin;

import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.model.UserStatus;
import com.nineties.alumni.auth.repo.UserRepository;
import com.nineties.alumni.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('platform_admin')")
public class AdminController {
  private final UserRepository userRepository;

  public AdminController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/users")
  public List<User> listUsers(@RequestParam(required = false) String query) {
    // Sprint 1: simple all-users listing. You can replace with search later.
    return userRepository.findAll();
  }

  @PostMapping("/users/{id}/ban")
  public User ban(@PathVariable Long id) {
    User u = userRepository.findById(id)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
    u.setStatus(UserStatus.BANNED);
    return userRepository.save(u);
  }

  @PostMapping("/users/{id}/unban")
  public User unban(@PathVariable Long id) {
    User u = userRepository.findById(id)
        .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
    u.setStatus(UserStatus.ACTIVE);
    return userRepository.save(u);
  }
}
