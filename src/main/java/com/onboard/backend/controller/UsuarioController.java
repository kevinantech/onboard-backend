package com.onboard.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboard.backend.entity.Empresa;
import com.onboard.backend.entity.Particular;
import com.onboard.backend.entity.Usuario;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.service.UsuarioService;
import com.onboard.backend.service.UsuarioService.ResultadoLogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<Usuario> createUsuario(@RequestBody Map<String, Object> body) {
        ObjectMapper mapper = new ObjectMapper();

        Usuario usuario = mapper.convertValue(body.get("usuario"), Usuario.class);

        Object datosAdicionales = null;

        if (body.containsKey("particular")) {
            datosAdicionales = mapper.convertValue(body.get("particular"), Particular.class);
        } else if (body.containsKey("empresa")) {
            datosAdicionales = mapper.convertValue(body.get("empresa"), Empresa.class);
        }

        Usuario saved = usuarioService.saveUsuario(usuario, datosAdicionales);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String correo, @RequestParam String password) {
        ResultadoLogin resultado = usuarioService.validarLogin(correo, password);

        switch (resultado) {
            case USUARIO_NO_ENCONTRADO:
                throw new InvalidInputException(
                        "User not found",
                        "USER_NOT_FOUND",
                        "No user was found with the provided email: " + correo);

            case CONTRASENA_INCORRECTA:
                throw new InvalidInputException(
                        "Incorrect password",
                        "INVALID_PASSWORD",
                        "The password provided for the user with email " + correo + " is incorrect");

            case USUARIO_PENDIENTE:
                throw new InvalidInputException(
                        "Account pending verification",
                        "ACCOUNT_PENDING",
                        "The account with email " + correo + " is pending verification");

            case USUARIO_RECHAZADO:
                throw new InvalidInputException(
                        "Account rejected",
                        "ACCOUNT_REJECTED",
                        "The account with email " + correo + " has been rejected");

            case USUARIO_SUSPENDIDO:
                throw new InvalidInputException(
                        "Account suspended",
                        "ACCOUNT_SUSPENDED",
                        "The account with email " + correo + " is currently suspended");

            case USUARIO_INACTIVO:
                throw new InvalidInputException(
                        "Account inactive",
                        "ACCOUNT_INACTIVE",
                        "The account with email " + correo + " is inactive");

            case EXITO:
                Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correo);
                return ResponseEntity.ok(usuario);

            default:
                throw new InvalidInputException(
                        "Unexpected error",
                        "UNEXPECTED_ERROR",
                        "An unexpected error occurred during login for email: " + correo);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable String id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuarioById(@PathVariable String id) {
        return usuarioService.deleteUsuarioById(id);
    }

    @PostMapping("/{id}/foto-perfil")
    public ResponseEntity<String> subirFotoPerfil(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            String url = usuarioService.subirFotoPerfil(id, file);
            return ResponseEntity.ok(url);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            throw new InvalidInputException(
                    "Failed to upload photo",
                    "PHOTO_UPLOAD_ERROR",
                    "An internal error occurred while trying to upload the user's photo");

        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable String id, @RequestBody Map<String, Object> body) {
        ObjectMapper mapper = new ObjectMapper();

        Usuario usuarioActualizado = mapper.convertValue(body.get("usuario"), Usuario.class);

        Object datosAdicionales = null;
        if (body.containsKey("particular")) {
            datosAdicionales = mapper.convertValue(body.get("particular"), Particular.class);
        } else if (body.containsKey("empresa")) {
            datosAdicionales = mapper.convertValue(body.get("empresa"), Empresa.class);
        }

        Optional<Usuario> usuarioExistente = usuarioService.getUsuarioById(id);

        if (usuarioExistente.isPresent()) {
            Usuario actualizado = usuarioService.updateUsuario(id, usuarioActualizado, datosAdicionales);
            return ResponseEntity.ok(actualizado);
        } else {
            throw new InvalidInputException(
                    "User not found",
                    "USER_NOT_FOUND",
                    "No user was found with the provided id: " + id);
        }
    }

    @PutMapping("/verificacion/{id}")
    public ResponseEntity<Usuario> verificarUsuario(
            @PathVariable String id,
            @RequestParam String estado) {
        Usuario usuario = usuarioService.actualizarEstadoVerificacion(id, estado.toUpperCase());
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Usuario>> obtenerUsuariosPendientes() {
        List<Usuario> pendientes = usuarioService.getUsuariosPendientes();
        return ResponseEntity.ok(pendientes);
    }

}
