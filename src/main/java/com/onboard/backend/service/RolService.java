package com.onboard.backend.service;

import com.onboard.backend.entity.Rol;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Rol saveRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public Optional<Rol> getRolById(String idRol) {
        return rolRepository.findById(idRol);
    }

    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    public void deleteRolById(String idRol) {
        rolRepository.deleteById(idRol);
    }

    public Rol updateRol(String id, Rol rolActualizado) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException(
                        "Role not found",
                        "ROLE_NOT_FOUND",
                        "Cannot update role. No role exists in the database with ID: " + id));

        rolRepository.delete(rolExistente);
        return rolRepository.save(rolActualizado);
    }

}
