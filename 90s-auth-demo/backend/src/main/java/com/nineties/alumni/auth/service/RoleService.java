package com.nineties.alumni.auth.service;

import com.nineties.alumni.auth.model.Role;
import com.nineties.alumni.auth.model.UserRole;
import com.nineties.alumni.auth.repo.RoleRepository;
import com.nineties.alumni.auth.repo.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;

  public RoleService(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
    this.roleRepository = roleRepository;
    this.userRoleRepository = userRoleRepository;
  }

  public Role requireRole(String name) {
    return roleRepository.findByName(name).orElseThrow(() -> new IllegalStateException("Missing role: " + name));
  }

  public void assignPlatformRole(String userId, String roleName) {
    Role role = requireRole(roleName);
    UserRole ur = new UserRole();
    ur.setUserId(userId);
    ur.setRoleId(role.getId());
    ur.setScopeType("PLATFORM");
    ur.setScopeId(null);
    userRoleRepository.save(ur);
  }

  public void assignSpaceRole(String userId, String spaceId, String roleName) {
    Role role = requireRole(roleName);
    UserRole ur = new UserRole();
    ur.setUserId(userId);
    ur.setRoleId(role.getId());
    ur.setScopeType("SPACE");
    ur.setScopeId(spaceId);
    userRoleRepository.save(ur);
  }

  public List<String> listPlatformRoleNames(String userId) {
    List<UserRole> urs = userRoleRepository.findByUserIdAndScopeType(userId, "PLATFORM");
    List<String> names = new ArrayList<>();
    for (UserRole ur : urs) {
      roleRepository.findById(ur.getRoleId()).ifPresent(r -> names.add(r.getName()));
    }
    return names;
  }
}
