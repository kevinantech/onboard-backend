package com.onboard.backend.repository;

import com.onboard.backend.entity.Alquiler;
import com.onboard.backend.model.EstadoAlquiler;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlquilerRepository extends MongoRepository<Alquiler, String> {
    List<Alquiler> findByEstado(EstadoAlquiler estado);

    Optional<Alquiler> findByIdReserva(String idReserva);

}
