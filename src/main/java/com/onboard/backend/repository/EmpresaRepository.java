package com.onboard.backend.repository;

import com.onboard.backend.entity.Empresa;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmpresaRepository extends MongoRepository<Empresa, String> {
}
