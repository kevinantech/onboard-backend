package com.onboard.backend.controller;

import com.onboard.backend.entity.Factura;
import com.onboard.backend.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @PostMapping
    public ResponseEntity<Factura> createFactura(@RequestBody Factura factura) {
        Factura saved = facturaService.saveFactura(factura);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> getFacturaById(@PathVariable String id) {
        Optional<Factura> factura = facturaService.getFacturaById(id);
        return factura.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Factura>> getAllFacturas() {
        List<Factura> lista = facturaService.getAllFacturas();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable String id) {
        facturaService.deleteFacturaById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/por-cliente/{idCliente}")
    public List<Factura> obtenerFacturasPorIdCliente(@PathVariable String idCliente) {
        return facturaService.obtenerFacturasPorIdCliente(idCliente);
    }
}
