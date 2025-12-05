package com.onboard.backend.service;

import com.onboard.backend.entity.Particular;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.repository.ParticularRepository;
import com.onboard.backend.util.ValidationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticularService {

    @Autowired
    private ParticularRepository particularRepository;

    public Particular saveParticular(Particular particular) {
        String licencia = particular.getLicenciaConduccion();

        if (licencia != null && !licencia.isBlank()) {
            if (!ValidationUtils.isValidLicenciaConduccion(licencia)) {
                throw new InvalidInputException(
                        "Licencia de conducción inválida",
                        "INVALID_LICENSE_FORMAT",
                        "La licencia debe contener entre 6 y 12 dígitos numéricos.");
            }
            particular.setLicenciaConduccion(licencia.trim());
        }

        return particularRepository.save(particular);
    }

    public Optional<Particular> getParticularById(String idUsuario) {
        return particularRepository.findById(idUsuario);
    }

    public List<Particular> getAllParticulares() {
        return particularRepository.findAll();
    }

    public void deleteParticularById(String idUsuario) {
        particularRepository.deleteById(idUsuario);
    }

    public Particular updateParticular(Particular datosActualizados) {
        Particular existente = particularRepository.findById(datosActualizados.getIdUsuario())
                .orElseThrow(() -> new InvalidInputException(
                        "Usuario no encontrado",
                        "USER_NOT_FOUND",
                        "No se encontró un particular con ID: " + datosActualizados.getIdUsuario()));

        String licencia = datosActualizados.getLicenciaConduccion();
        if (licencia != null && !licencia.isBlank()) {
            if (!ValidationUtils.isValidLicenciaConduccion(licencia)) {
                throw new InvalidInputException(
                        "Licencia de conducción inválida",
                        "INVALID_LICENSE_FORMAT",
                        "La licencia debe contener entre 6 y 12 dígitos numéricos.");
            }
            existente.setLicenciaConduccion(licencia.trim());
        }

        return particularRepository.save(existente);
    }

}
