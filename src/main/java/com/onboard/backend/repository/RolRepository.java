package com.onboard.backend.repository;
import java.util.Optional;
import com.onboard.backend.entity.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RolRepository extends MongoRepository<Rol, String> {
    Optional<Rol> findByRol(String rol);
}
