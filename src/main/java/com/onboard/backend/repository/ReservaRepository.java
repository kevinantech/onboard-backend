package com.onboard.backend.repository;

import com.onboard.backend.entity.Reserva;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservaRepository extends MongoRepository<Reserva, String> {
    List <Reserva> findAllByIdCliente(String idCliente);
    List<Reserva> findAllByIdVehiculo(String idVehiculo);
    

}
