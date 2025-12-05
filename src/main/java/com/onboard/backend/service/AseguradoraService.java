package com.onboard.backend.service;

import com.onboard.backend.entity.Aseguradora;
import com.onboard.backend.repository.AseguradoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AseguradoraService {

    @Autowired
    private AseguradoraRepository aseguradoraRepository;

    public Aseguradora saveAseguradora(Aseguradora aseguradora) {
        return aseguradoraRepository.save(aseguradora);
    }

    public Optional<Aseguradora> getAseguradoraById(String id) {
        return aseguradoraRepository.findById(id);
    }

    public List<Aseguradora> getAllAseguradoras() {
        return aseguradoraRepository.findAll();
    }

    public void deleteAseguradoraById(String id) {
        aseguradoraRepository.deleteById(id);
    }
}
