package com.nineties.alumni.space.repo;

import com.nineties.alumni.space.model.SpaceInviteCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceInviteCodeRepository extends JpaRepository<SpaceInviteCode, Long> {
  Optional<SpaceInviteCode> findByCode(String code);
}
