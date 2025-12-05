package com.onboard.backend.controller;

import com.onboard.backend.entity.Aseguradora;
import com.onboard.backend.service.AseguradoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aseguradoras")
public class AseguradoraController {

    @Autowired
    private AseguradoraService aseguradoraService;

    @PostMapping
    public ResponseEntity<Aseguradora> createAseguradora(@RequestBody Aseguradora aseguradora) {
        Aseguradora saved = aseguradoraService.saveAseguradora(aseguradora);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aseguradora> getAseguradoraById(@PathVariable String id) {
        Optional<Aseguradora> aseguradora = aseguradoraService.getAseguradoraById(id);
        return aseguradora.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Aseguradora>> getAllAseguradoras() {
        List<Aseguradora> lista = aseguradoraService.getAllAseguradoras();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAseguradora(@PathVariable String id) {
        aseguradoraService.deleteAseguradoraById(id);
        return ResponseEntity.noContent().build();
    }
}
