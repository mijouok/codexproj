package com.nineties.alumni.auth.repo;

import com.nineties.alumni.auth.model.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {
  List<UserRole> findByUserIdAndScopeType(String userId, String scopeType);
  List<UserRole> findByUserIdAndScopeTypeAndScopeId(String userId, String scopeType, String scopeId);
}
