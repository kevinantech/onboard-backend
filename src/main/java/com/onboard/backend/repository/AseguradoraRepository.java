package com.onboard.backend.repository;

import com.onboard.backend.entity.Aseguradora;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AseguradoraRepository extends MongoRepository<Aseguradora, String> {
}
