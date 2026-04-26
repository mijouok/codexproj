package com.nineties.alumni.space.repo;

import com.nineties.alumni.space.model.Space;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpaceRepository extends MongoRepository<Space, String> {
  Optional<Space> findBySlug(String slug);
}
