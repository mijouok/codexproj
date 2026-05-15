package com.nineties.alumni.home.repo;

import com.nineties.alumni.home.model.HomeStatusPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HomeStatusPostRepository extends MongoRepository<HomeStatusPost, String> {
  Optional<HomeStatusPost> findTopByUserIdOrderByCreatedAtDesc(String userId);

  List<HomeStatusPost> findTop20ByUserIdInOrderByCreatedAtDesc(Collection<String> userIds);
}
