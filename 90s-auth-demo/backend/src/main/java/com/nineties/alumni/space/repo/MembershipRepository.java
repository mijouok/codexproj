package com.nineties.alumni.space.repo;

import com.nineties.alumni.space.model.Membership;
import com.nineties.alumni.space.model.MembershipStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends MongoRepository<Membership, String> {
  Optional<Membership> findBySpaceIdAndUserId(String spaceId, String userId);
  List<Membership> findByUserIdAndMembershipStatus(String userId, MembershipStatus status);
  List<Membership> findBySpaceIdInAndMembershipStatus(Collection<String> spaceIds, MembershipStatus status);
}
