package com.onboard.backend.controller;

import com.onboard.backend.entity.Pago;
import com.onboard.backend.service.PagoService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/crear")
    public ResponseEntity<String> crearPago(@RequestParam String idFactura) {
        try {
            String approvalUrl = pagoService.crearPago(idFactura);
            return ResponseEntity.ok(approvalUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear pago: " + e.getMessage());
        }
    }

    @PostMapping("/capturar")
    public ResponseEntity<?> capturarPago(@RequestParam String orderId) {
        try {
            return pagoService.capturarPago(orderId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al capturar pago: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodosLosPagos() {
        try {
            return ResponseEntity.ok(pagoService.obtenerTodosLosPagos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener los pagos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> getPagoById(@PathVariable String id) {
        return pagoService.getPagoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPago(@PathVariable String id) {
        try {
            pagoService.eliminarPago(id);
            return ResponseEntity.ok("Pago eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar pago: " + e.getMessage());
        }
    }

}
