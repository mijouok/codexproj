package com.nineties.alumni;

import com.nineties.alumni.auth.model.Role;
import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.model.UserStatus;
import com.nineties.alumni.auth.repo.RoleRepository;
import com.nineties.alumni.auth.repo.UserRepository;
import com.nineties.alumni.auth.service.RoleNames;
import com.nineties.alumni.auth.service.RoleService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(DataInitializer.DemoAdminProperties.class)
public class DataInitializer {

  @Bean
  CommandLineRunner init(RoleRepository roleRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         RoleService roleService,
                         DemoAdminProperties demoAdminProperties) {
    return args -> {
      ensureRole(roleRepository, RoleNames.PLATFORM_ADMIN);
      ensureRole(roleRepository, RoleNames.MEMBER);

      if (!demoAdminProperties.isEnabled()) {
        return;
      }
      if (demoAdminProperties.getPassword() == null || demoAdminProperties.getPassword().isBlank()) {
        throw new IllegalStateException("DEMO_ADMIN_PASSWORD must be configured when demo admin is enabled");
      }
      String adminEmail = demoAdminProperties.getEmail();
      if (!userRepository.existsByEmail(adminEmail)) {
        User u = new User();
        u.setEmail(adminEmail);
        u.setNickname("Platform Admin");
        u.setPasswordHash(passwordEncoder.encode(demoAdminProperties.getPassword()));
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

  @ConfigurationProperties(prefix = "app.demo-admin")
  public static class DemoAdminProperties {
    private boolean enabled;
    private String email;
    private String password;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }
}
