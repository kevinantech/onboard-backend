package com.onboard.backend.repository;

import com.onboard.backend.entity.Factura;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface FacturaRepository extends MongoRepository<Factura, String> {
    Optional<Factura> findByIdReserva(String idReserva);
}
