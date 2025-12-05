package com.onboard.backend.service;

import com.onboard.backend.entity.Factura;
import com.onboard.backend.entity.Reserva;
import com.onboard.backend.repository.FacturaRepository;
import com.onboard.backend.repository.ReservaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    public Factura saveFactura(Factura factura) {
        return facturaRepository.save(factura);
    }

    public Optional<Factura> getFacturaById(String idFactura) {
        return facturaRepository.findById(idFactura);
    }

    public List<Factura> getAllFacturas() {
        return facturaRepository.findAll();
    }

    public void deleteFacturaById(String idFactura) {
        facturaRepository.deleteById(idFactura);
    }

    public Factura getFacturaByIdReserva(String idReserva) {
        return facturaRepository.findByIdReserva(idReserva).get();
    }

    public List<Factura> obtenerFacturasPorIdCliente(String idCliente) {
        List<Reserva> reservas = reservaRepository.findAllByIdCliente(idCliente);

        List<String> idsReserva = reservas.stream()
                .map(Reserva::getIdReserva)
                .toList();

        List<Factura> facturas = facturaRepository.findAll()
                .stream()
                .filter(factura -> idsReserva.contains(factura.getIdReserva()))
                .toList();

        return facturas;
    }
}
