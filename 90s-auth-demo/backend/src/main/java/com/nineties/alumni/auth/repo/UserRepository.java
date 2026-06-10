package com.nineties.alumni.auth.repo;

import com.nineties.alumni.auth.model.User;
import com.nineties.alumni.auth.model.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);
  Optional<User> findByPhone(String phone);
  boolean existsByEmail(String email);
  boolean existsByPhone(String phone);

  List<User> findByStatusNot(UserStatus status, Pageable pageable);

  List<User> findTop10ByStatusAndIdNotIn(UserStatus status, Collection<String> excludedIds);
}
