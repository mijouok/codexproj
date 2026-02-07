package com.nineties.alumni.space.repo;

import com.nineties.alumni.space.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {
  Optional<Space> findBySlug(String slug);
}
