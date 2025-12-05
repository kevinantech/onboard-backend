package com.onboard.backend.controller;

import com.onboard.backend.entity.Particular;
import com.onboard.backend.service.ParticularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/particulares")
public class ParticularController {

    @Autowired
    private ParticularService particularService;

    @PostMapping
    public ResponseEntity<Particular> createParticular(@RequestBody Particular particular) {
        Particular saved = particularService.saveParticular(particular);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Particular> getParticularById(@PathVariable String id) {
        Optional<Particular> particular = particularService.getParticularById(id);
        return particular.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Particular>> getAllParticulares() {
        List<Particular> particulares = particularService.getAllParticulares();
        return ResponseEntity.ok(particulares);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticularById(@PathVariable String id) {
        particularService.deleteParticularById(id);
        return ResponseEntity.noContent().build();
    }
}
