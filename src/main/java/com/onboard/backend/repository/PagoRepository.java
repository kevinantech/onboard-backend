package com.onboard.backend.repository;

import com.onboard.backend.entity.Pago;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PagoRepository extends MongoRepository<Pago, String> {
    List<Pago> findByIdFactura(String idFactura);
}
