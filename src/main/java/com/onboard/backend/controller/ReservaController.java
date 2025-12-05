package com.onboard.backend.controller;

import com.onboard.backend.entity.Factura;
import com.onboard.backend.entity.Reserva;
import com.onboard.backend.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<Reserva> createReserva(@RequestBody Reserva reserva) {
        Reserva saved = reservaService.saveReserva(reserva);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable String id) {
        Optional<Reserva> reserva = reservaService.getReservaById(id);
        return reserva.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = reservaService.getAllReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<List<Reserva>> getAllReservasByIdUsuario(@PathVariable("id") String id) {
        return ResponseEntity.ok(reservaService.getAllReservasByIdCliente(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservaById(@PathVariable String id) {
        reservaService.deleteReservaById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fechas-reservadas")
    public ResponseEntity<List<String>> getFechasReservadas(@RequestParam String idVehiculo) {
        List<String> fechas = reservaService.getFechasReservadasPorVehiculo(idVehiculo);
        return ResponseEntity.ok(fechas);
    }

    @GetMapping("/{idReserva}/factura")
    public ResponseEntity<Factura> getFacturaDeReserva(@PathVariable String idReserva) {
        Factura factura = reservaService.getFactura(idReserva);
        return ResponseEntity.ok(factura);
    }

    @GetMapping("/propietario/{idPropietario}")
    public ResponseEntity<List<Reserva>> getReservasByIdPropietario(@PathVariable String idPropietario) {
        List<Reserva> reservas = reservaService.getReservasByIdPropietario(idPropietario);
        return ResponseEntity.ok(reservas);
    }

}
