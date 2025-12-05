package com.onboard.backend.repository;

import com.onboard.backend.entity.Vehiculo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehiculoRepository extends MongoRepository<Vehiculo, String> {
    List<Vehiculo> findTop6ByOrderByCantidadAlquilerDesc();

    List<Vehiculo> findTop6ByOrderByFechaRegistroDesc();

    List<Vehiculo> findByTipoVehiculo(String tipoVehiculo);

    List<Vehiculo> findByTipoTerreno(String tipoTerreno);

    List<Vehiculo> findByTipoTransmision(String tipoTransmision);

    List<Vehiculo> findByCombustible(String combustible);

    List<Vehiculo> findAllByOrderByFechaRegistroAsc();

    List<Vehiculo> findAllByOrderByFechaRegistroDesc();

    List<Vehiculo> findAllByIdPropietario(String idPropietario);

    List<Vehiculo> findByCapacidadPasajeros(int capacidadPasajeros);

    List<Vehiculo> findByCapacidadPasajerosGreaterThanEqualAndCapacidadPasajerosLessThanEqual(int min, int max);

    List<Vehiculo> findAllByOrderByCantidadAlquilerDesc();

    List<Vehiculo> findAllByOrderByCantidadAlquilerAsc();

    

}
