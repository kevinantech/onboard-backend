package com.onboard.backend.service;

import com.onboard.backend.entity.Empresa;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.repository.EmpresaRepository;
import com.onboard.backend.util.ValidationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public Empresa saveEmpresa(Empresa empresa) {
        if (empresa.getIdUsuario() == null || empresa.getIdUsuario().trim().isEmpty()) {
            throw new InvalidInputException("Invalid user ID", "INVALID_USER_ID",
                    "The user ID cannot be null or empty.");
        }

        if (!ValidationUtils.isValidRepresentante(empresa.getRepresentante())) {
            throw new InvalidInputException("Invalid representative name", "INVALID_REPRESENTATIVE",
                    "Representative name must be between 3 and 100 characters and cannot be empty.");
        }

        ValidationUtils.validarDocumento(empresa.getDocumentoRepresentante(), empresa.getTipoDocumentoRepresentante());

        if (empresaRepository.existsById(empresa.getIdUsuario())) {
            throw new InvalidInputException("Empresa already exists", "EMPRESA_ALREADY_EXISTS",
                    "An empresa with this user ID already exists.");
        }

        return empresaRepository.save(empresa);
    }

    public Optional<Empresa> getEmpresaById(String idUsuario) {
        return empresaRepository.findById(idUsuario);
    }

    public List<Empresa> getAllEmpresas() {
        return empresaRepository.findAll();
    }

    public void deleteEmpresaById(String idUsuario) {
        empresaRepository.deleteById(idUsuario);
    }

    public Empresa updateEmpresa(Empresa empresa) {
        Empresa existente = empresaRepository.findById(empresa.getIdUsuario())
                .orElseThrow(() -> new InvalidInputException(
                        "Empresa no encontrada",
                        "EMPRESA_NOT_FOUND",
                        "No se encontró una empresa con ID de usuario: " + empresa.getIdUsuario()));

        if (empresa.getRepresentante() != null) {
            if (!ValidationUtils.isValidRepresentante(empresa.getRepresentante())) {
                throw new InvalidInputException("Invalid representative name", "INVALID_REPRESENTATIVE",
                        "Representative name must be between 3 and 100 characters and cannot be empty.");
            }
            existente.setRepresentante(empresa.getRepresentante());
        }
        if ((empresa.getDocumentoRepresentante() != null && empresa.getDocumentoRepresentante().isBlank())
                && empresa.getTipoDocumentoRepresentante() != null) {
            ValidationUtils.validarDocumento(empresa.getDocumentoRepresentante(),
                    empresa.getTipoDocumentoRepresentante());
        }
        return empresaRepository.save(existente);
    }

}
