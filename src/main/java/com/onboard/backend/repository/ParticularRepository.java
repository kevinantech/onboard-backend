package com.onboard.backend.repository;

import com.onboard.backend.entity.Particular;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParticularRepository extends MongoRepository<Particular, String> {
}
