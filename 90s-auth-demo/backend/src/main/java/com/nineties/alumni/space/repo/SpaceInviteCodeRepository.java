package com.nineties.alumni.space.repo;

import com.nineties.alumni.space.model.SpaceInviteCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpaceInviteCodeRepository extends MongoRepository<SpaceInviteCode, String> {
  Optional<SpaceInviteCode> findByCode(String code);
}
