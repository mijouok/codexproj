package com.nineties.alumni;

import com.nineties.alumni.auth.model.Role;
import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.model.UserStatus;
import com.nineties.alumni.auth.repo.RoleRepository;
import com.nineties.alumni.auth.repo.UserRepository;
import com.nineties.alumni.auth.service.RoleNames;
import com.nineties.alumni.auth.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

  @Bean
  CommandLineRunner init(RoleRepository roleRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         RoleService roleService) {
    return args -> {
      ensureRole(roleRepository, RoleNames.PLATFORM_ADMIN);
      ensureRole(roleRepository, RoleNames.SPACE_OWNER);
      ensureRole(roleRepository, RoleNames.SPACE_ADMIN);
      ensureRole(roleRepository, RoleNames.SPACE_MODERATOR);
      ensureRole(roleRepository, RoleNames.MEMBER);

      // Default admin for demo
      String adminEmail = "admin@90s.demo";
      if (!userRepository.existsByEmail(adminEmail)) {
        User u = new User();
        u.setEmail(adminEmail);
        u.setNickname("Platform Admin");
        u.setPasswordHash(passwordEncoder.encode("Admin123!"));
        u.setStatus(UserStatus.ACTIVE);
        userRepository.save(u);
        roleService.assignPlatformRole(u.getId(), RoleNames.PLATFORM_ADMIN);
      }
    };
  }

  private void ensureRole(RoleRepository roleRepository, String name) {
    roleRepository.findByName(name).orElseGet(() -> {
      Role r = new Role();
      r.setName(name);
      return roleRepository.save(r);
    });
  }
}
