package com.onboard.backend.controller;

import com.onboard.backend.entity.Alquiler;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.service.AlquilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    @PostMapping
    public ResponseEntity<Alquiler> createAlquiler(@RequestBody Alquiler alquiler) {
        Alquiler saved = alquilerService.saveAlquiler(alquiler);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> getAlquilerById(@PathVariable String id) {
        Optional<Alquiler> alquiler = alquilerService.getAlquilerById(id);
        return alquiler.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Alquiler>> getAllAlquileres() {
        List<Alquiler> lista = alquilerService.getAllAlquileres();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlquiler(@PathVariable String id) {
        alquilerService.deleteAlquilerById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/propietario/{idPropietario}/estado/{estado}")
    public ResponseEntity<List<Alquiler>> getAlquileresByPropietarioAndEstado(
            @PathVariable String idPropietario,
            @PathVariable String estado) {
        try {
            List<Alquiler> alquileres = alquilerService.getAlquileresByPropietarioIdAndEstado(idPropietario, estado);
            return ResponseEntity.ok(alquileres);
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Alquiler> actualizarEstadoAlquiler(
            @PathVariable String id,
            @RequestParam String nuevoEstado) {
        try {
            Alquiler actualizado = alquilerService.actualizarEstadoAlquiler(id, nuevoEstado);
            return ResponseEntity.ok(actualizado);
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/propietario/{idPropietario}")
    public ResponseEntity<List<Alquiler>> getAlquileresByPropietario(@PathVariable String idPropietario) {
        List<Alquiler> alquileres = alquilerService.getAlquileresByPropietarioId(idPropietario);
        return ResponseEntity.ok(alquileres);
    }


    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Alquiler>> getAlquileresByIdCliente(@PathVariable String idCliente) {
        List<Alquiler> alquileres = alquilerService.getAlquileresByIdCliente(idCliente);
        return ResponseEntity.ok(alquileres);
    }

    @GetMapping("/cliente/{idCliente}/activos")
    public ResponseEntity<List<Alquiler>> getAlquileresActivosByIdCliente(@PathVariable String idCliente) {
        List<Alquiler> alquileres = alquilerService.getAlquileresActivosByIdCliente(idCliente);
        return ResponseEntity.ok(alquileres);
    }

}
