package com.nineties.alumni.auth.repo;

import com.nineties.alumni.auth.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
  List<UserRole> findByUserIdAndScopeType(Long userId, String scopeType);
  List<UserRole> findByUserIdAndScopeTypeAndScopeId(Long userId, String scopeType, Long scopeId);
}
