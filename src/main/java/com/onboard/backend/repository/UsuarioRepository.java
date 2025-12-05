package com.onboard.backend.repository;

import com.onboard.backend.entity.Usuario;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Usuario findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsById(@NonNull String id);
    List<Usuario> findByEstadoVerificacion(String estadoVerificacion);
}
