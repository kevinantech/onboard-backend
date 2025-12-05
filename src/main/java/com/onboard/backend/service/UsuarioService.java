package com.onboard.backend.service;

import com.onboard.backend.entity.Empresa;
import com.onboard.backend.entity.Particular;
import com.onboard.backend.entity.Rol;
import com.onboard.backend.entity.Usuario;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.model.EstadoVerificacion;
import com.onboard.backend.repository.RolRepository;
import com.onboard.backend.repository.UsuarioRepository;
import com.onboard.backend.security.EncriptadorAESGCM;
import com.onboard.backend.util.FormatUtils;
import com.onboard.backend.util.ValidationUtils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.apache.commons.lang3.StringUtils;

@Service
public class UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum ResultadoLogin {
        EXITO,
        USUARIO_NO_ENCONTRADO,
        CONTRASENA_INCORRECTA,
        USUARIO_PENDIENTE,
        USUARIO_RECHAZADO,
        USUARIO_SUSPENDIDO,
        USUARIO_INACTIVO,
        ERROR_DESCONOCIDO
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private ParticularService particularService;

    @Transactional
    public Usuario saveUsuario(Usuario usuario, Object datosAdicionales) {

        usuario.setNombre(FormatUtils.capitalizarNombre(usuario.getNombre()));
        usuario.setCorreo(FormatUtils.formatearCorreo(usuario.getCorreo()));
        usuario.setDireccion(FormatUtils.limpiarCadena(usuario.getDireccion()));
        usuario.setTelefono(FormatUtils.formatPhoneNumber(usuario.getTelefono()));

        ValidationUtils.validarDocumento(usuario.getIdUsuario(), usuario.getTipoIdentificacion());

        if (!ValidationUtils.isValidEmail(usuario.getCorreo())) {
            throw new InvalidInputException(
                    "Invalid email format",
                    "INVALID_EMAIL_FORMAT",
                    "Email does not match standard format: " + usuario.getCorreo()
                            + ". Example of a valid email: user@example.com");
        }

        Rol rol = rolRepository.findByRol(usuario.getIdRol())
                .orElseThrow(() -> new InvalidInputException(
                        "Invalid role format",
                        "INVALID_ROLE_FORMAT",
                        "Role does not exist: " + usuario.getIdRol()
                                + ". Valid examples: company_owner, company_dual, company_client, individual_client, individual_owner, individual_dual"));

        usuario.setIdRol(rol.getIdRol());

        /*
         * if (!ValidationUtils.isValidCuentaBancaria(usuario.getCuentaBancaria())) {
         * throw new
         * InvalidInputException("Invalid bank account format. Only digits allowed (10–20 characters)."
         * , "");
         * }
         */

        if (!ValidationUtils.isValidNombre(usuario.getNombre())) {
            throw new InvalidInputException(
                    "Invalid name",
                    "INVALID_NAME_FORMAT",
                    "Name field contains invalid characters or is empty. Example of a valid name: María González");
        }

        if (!ValidationUtils.isValidTelefono(usuario.getTelefono())) {
            throw new InvalidInputException(
                    "Invalid phone number",
                    "INVALID_PHONE_NUMBER_FORMAT",
                    "Phone number format is incorrect or unsupported. Example of a valid phone number: 3001234567");

        }

        if (!ValidationUtils.isValidDireccion(usuario.getDireccion())) {
            throw new InvalidInputException(
                    "Invalid address",
                    "INVALID_ADDRESS_FORMAT",
                    "Address must be between 5 and 150 characters. Example of a valid address: Calle 45 #12-34, Bogotá");
        }

        if (usuarioRepository.existsById(usuario.getIdUsuario())) {
            throw new InvalidInputException(
                    "ID already registered",
                    "DUPLICATE_ID",
                    "A user is already registered with the ID: " + usuario.getIdUsuario());
        }

        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new InvalidInputException(
                    "Email already registered",
                    "DUPLICATE_EMAIL",
                    "A user is already registered with the email: " + usuario.getCorreo());
        }

        String hashedPass = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hashedPass);
        usuario.setFechaRegistro(LocalDateTime.now());
        if (StringUtils.isNotBlank(usuario.getCuentaBancaria())) {
            try {
                String cuentaEncriptada = EncriptadorAESGCM.encriptar(usuario.getCuentaBancaria());
                usuario.setCuentaBancaria(cuentaEncriptada);
            } catch (Exception e) {
                logger.error("Error encrypting bank account for user with ID: " + usuario.getIdUsuario() + "\n", e);
                throw new InvalidInputException(
                        "Unable to encrypt bank account",
                        "BANK_ACCOUNT_ENCRYPTION_ERROR",
                        "An internal error occurred while encrypting the bank account information. Please contact the administrator.");
            }
        }
        usuario.setFechaRegistro(LocalDateTime.now());
        try {

            String nombreRol = rol.getRol().toLowerCase();

            if (nombreRol.contains("individual")) {
                if (!(datosAdicionales instanceof Particular)) {
                    throw new InvalidInputException(
                            "Invalid additional data for individual",
                            "INVALID_PARTICULAR_DATA",
                            "The additional data provided does not match the 'individual' user role.");
                }
                Particular particular = (Particular) datosAdicionales;
                particular.setIdUsuario(usuario.getIdUsuario());
                particularService.saveParticular(particular);

            } else if (nombreRol.contains("company")) {
                if (!(datosAdicionales instanceof Empresa)) {
                    throw new InvalidInputException(
                            "Invalid additional data for company",
                            "INVALID_COMPANY_DATA",
                            "The additional data provided does not match the 'company' user role.");
                }
                Empresa empresa = (Empresa) datosAdicionales;
                empresa.setIdUsuario(usuario.getIdUsuario());
                empresaService.saveEmpresa(empresa);
            }

            usuario.setEstadoVerificacion(EstadoVerificacion.PENDIENTE);

            emailService.enviarCorreoEstadoCuenta(usuario.getCorreo(), usuario.getNombre(),
                    usuario.getEstadoVerificacion());

            usuario.setFotoPerfilUrl(
                    "https://vbtvwszlngotrdoqcanb.supabase.co/storage/v1/object/public/resources/profile_photo/default_photo.jpg");
            return usuarioRepository.save(usuario);

        } catch (Exception e) {
            throw e;
        }
    }

    public Optional<Usuario> getUsuarioById(String id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new InvalidInputException(
                    "User not found",
                    "USER_NOT_FOUND",
                    "The user with ID '" + id
                            + "' was not found in the database while uploading the profile photo");
        }
        return usuarioOpt;
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public ResponseEntity<String> deleteUsuarioById(String id) {

        Usuario usuario = getUsuarioById(id).get();

        if (usuario.getEstadoVerificacion() == EstadoVerificacion.INACTIVO) {
            logger.info("User with ID '{}' is already inactive. No changes were made.", id);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already inactive. No action taken.");
        }

        usuario.setEstadoVerificacion(EstadoVerificacion.INACTIVO);
        usuarioRepository.save(usuario);

        emailService.enviarCorreoEstadoCuenta(usuario.getCorreo(), usuario.getNombre(),
                usuario.getEstadoVerificacion());
        return ResponseEntity.ok("User deactivated successfully.");
    }

    public ResultadoLogin validarLogin(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return ResultadoLogin.USUARIO_NO_ENCONTRADO;
        }

        if (usuario.getEstadoVerificacion() != EstadoVerificacion.APROBADO) {
            switch (usuario.getEstadoVerificacion()) {
                case PENDIENTE:
                    return ResultadoLogin.USUARIO_PENDIENTE;
                case RECHAZADO:
                    return ResultadoLogin.USUARIO_RECHAZADO;
                case SUSPENDIDO:
                    return ResultadoLogin.USUARIO_SUSPENDIDO;
                case INACTIVO:
                    return ResultadoLogin.USUARIO_INACTIVO;
                default:
                    return ResultadoLogin.ERROR_DESCONOCIDO;
            }
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return ResultadoLogin.CONTRASENA_INCORRECTA;
        }

        return ResultadoLogin.EXITO;
    }

    public Usuario obtenerUsuarioPorCorreo(String correo) {

        return usuarioRepository.findByCorreo(correo);
    }

    public String subirFotoPerfil(String usuarioId, MultipartFile file) throws IOException {
        String fileUrl = fileUploadService.uploadProfilePhoto(file, usuarioId);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new InvalidInputException(
                    "User not found",
                    "USER_NOT_FOUND",
                    "The user with ID '" + usuarioId
                            + "' was not found in the database while uploading the profile photo");
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getFotoPerfilUrl() != null && !usuario.getFotoPerfilUrl().toString().equals(
                "https://vbtvwszlngotrdoqcanb.supabase.co/storage/v1/object/public/resources/profile_photo/default_photo.jpg")) {
            fileUploadService.deletePhotoByUrl(usuario.getFotoPerfilUrl());
        }
        usuario.setFotoPerfilUrl(fileUrl);

        usuarioRepository.save(usuario);

        return fileUrl;
    }

    @Transactional
    public Usuario updateUsuario(String id, Usuario usuarioActualizado, Object datosAdicionales) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException(
                        "User not found",
                        "USER_NOT_FOUND",
                        "Cannot update user. No user exists in the database with ID: " + id));

        if (usuarioActualizado.getCorreo() != null
                && !usuarioActualizado.getCorreo().equals(usuarioExistente.getCorreo())) {
            if (!ValidationUtils.isValidEmail(usuarioActualizado.getCorreo())) {
                throw new InvalidInputException(
                        "Invalid email format",
                        "INVALID_EMAIL_FORMAT",
                        "Email does not match standard format: " + usuarioActualizado.getCorreo());
            }
            usuarioExistente.setCorreo(FormatUtils.formatearCorreo(usuarioActualizado.getCorreo()));
        }

        if (usuarioActualizado.getNombre() != null) {
            if (!ValidationUtils.isValidNombre(usuarioActualizado.getNombre())) {
                throw new InvalidInputException(
                        "Invalid name",
                        "INVALID_NAME_FORMAT",
                        "Invalid name: " + usuarioActualizado.getNombre());
            }
            usuarioExistente.setNombre(FormatUtils.capitalizarNombre(usuarioActualizado.getNombre()));
        }

        if (usuarioActualizado.getPassword() != null) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        if (usuarioActualizado.getTelefono() != null) {
            if (!ValidationUtils.isValidTelefono(usuarioActualizado.getTelefono())) {
                throw new InvalidInputException(
                        "Invalid phone number",
                        "INVALID_PHONE_NUMBER_FORMAT",
                        "Invalid phone number: " + usuarioActualizado.getTelefono());
            }
            usuarioExistente.setTelefono(FormatUtils.formatPhoneNumber(usuarioActualizado.getTelefono()));
        }

        if (usuarioActualizado.getDireccion() != null) {
            if (!ValidationUtils.isValidDireccion(usuarioActualizado.getDireccion())) {
                throw new InvalidInputException(
                        "Invalid address",
                        "INVALID_ADDRESS_FORMAT",
                        "Invalid address: " + usuarioActualizado.getDireccion());
            }
            usuarioExistente.setDireccion(FormatUtils.limpiarCadena(usuarioActualizado.getDireccion()));
        }

        if (usuarioActualizado.getCuentaBancaria() != null) {
            try {
                String cuentaEncriptada = EncriptadorAESGCM.encriptar(usuarioActualizado.getCuentaBancaria());
                usuarioExistente.setCuentaBancaria(cuentaEncriptada);
            } catch (Exception e) {
                logger.error("Error encrypting bank account", e);
                throw new InvalidInputException(
                        "Encryption error",
                        "BANK_ACCOUNT_ENCRYPTION_ERROR",
                        "An error occurred while encrypting the bank account information.");
            }
        }

        Rol rol = rolRepository.findById(usuarioExistente.getIdRol())
                .orElseThrow(() -> new InvalidInputException("Rol no encontrado", "ROL_NOT_FOUND", ""));

        String nombreRol = rol.getRol().toLowerCase();
        if (nombreRol.contains("individual") && datosAdicionales instanceof Particular particular) {
            particular.setIdUsuario(id);
            particularService.updateParticular(particular);
        } else if (nombreRol.contains("company") && datosAdicionales instanceof Empresa empresa) {
            empresa.setIdUsuario(id);
            empresaService.updateEmpresa(empresa);
        }

        if (usuarioActualizado.getIdRol() != null &&
                !usuarioActualizado.getIdRol().equals(usuarioExistente.getIdRol())) {

            if (!rolRepository.existsById(usuarioActualizado.getIdRol())) {
                throw new InvalidInputException(
                        "Invalid role ID",
                        "INVALID_ROLE_ID",
                        "The provided role ID does not exist in the system.");
            }

            usuarioExistente.setIdRol(usuarioActualizado.getIdRol());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario actualizarEstadoVerificacion(String idUsuario, String nuevoEstadoStr) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new InvalidInputException(
                    "User not found",
                    "USER_NOT_FOUND",
                    "There is no user registered with the provided ID");
        }

        Usuario usuario = usuarioOpt.get();

        EstadoVerificacion nuevoEstado;
        try {
            nuevoEstado = EstadoVerificacion.valueOf(nuevoEstadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(
                    "Invalid verification state",
                    "INVALID_STATE",
                    "The provided verification state is not valid");
        }

        if (usuario.getEstadoVerificacion() != null &&
                usuario.getEstadoVerificacion().equals(nuevoEstado)) {
            logger.info("El estado ya es '" + nuevoEstado + "' para el usuario con ID: " + idUsuario);
            return usuario;
        }

        usuario.setEstadoVerificacion(nuevoEstado);

        emailService.enviarCorreoEstadoCuenta(usuario.getCorreo(), usuario.getNombre(), nuevoEstado);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> getUsuariosPendientes() {
        return usuarioRepository.findByEstadoVerificacion("PENDIENTE");
    }

}
