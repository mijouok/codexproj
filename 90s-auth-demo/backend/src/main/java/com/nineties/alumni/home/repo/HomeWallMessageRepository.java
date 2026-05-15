package com.nineties.alumni.home.repo;

import com.nineties.alumni.home.model.HomeWallMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HomeWallMessageRepository extends MongoRepository<HomeWallMessage, String> {
  List<HomeWallMessage> findTop20ByToUserIdOrderByCreatedAtDesc(String toUserId);
}
