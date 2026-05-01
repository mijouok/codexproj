package com.nineties.alumni.space.repo;

import com.nineties.alumni.space.model.Membership;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends MongoRepository<Membership, String> {
  Optional<Membership> findBySpaceIdAndUserId(String spaceId, String userId);
  List<Membership> findByUserIdAndMembershipStatus(String userId, com.nineties.alumni.space.model.MembershipStatus status);
}
