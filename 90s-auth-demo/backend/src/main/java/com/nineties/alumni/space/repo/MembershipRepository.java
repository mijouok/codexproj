package com.nineties.alumni.space.repo;

import com.nineties.alumni.space.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
  Optional<Membership> findBySpaceIdAndUserId(Long spaceId, Long userId);
  List<Membership> findByUserIdAndMembershipStatus(Long userId, com.nineties.alumni.space.model.MembershipStatus status);
}
