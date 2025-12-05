package com.onboard.backend.service;

import com.onboard.backend.entity.Alquiler;
import com.onboard.backend.entity.Reserva;
import com.onboard.backend.entity.Vehiculo;
import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.model.EstadoAlquiler;
import com.onboard.backend.repository.AlquilerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AlquilerService {

    @Autowired
    @Lazy
    private ReservaService reservaService;

    @Autowired
    @Lazy
    private VehiculoService vehiculoService;

    @Autowired
    private AlquilerRepository alquilerRepository;

    private static final Logger schedulerLogger = LoggerFactory.getLogger(AlquilerService.class);

    public Alquiler saveAlquiler(Alquiler alquiler) {
        Reserva reserva = reservaService.getReservaById(alquiler.getIdReserva()).get();
        Vehiculo vehiculo = vehiculoService.getVehiculoById(reserva.getIdVehiculo()).get();

        vehiculoService.incrementarCantidadAlquiler(vehiculo);

        return alquilerRepository.save(alquiler);
    }

    public Optional<Alquiler> getAlquilerById(String idAlquiler) {
        Optional<Alquiler> alquiler = alquilerRepository.findById(idAlquiler);
        if (alquiler.isEmpty()) {
            throw new InvalidInputException(
                    "Alquiler no encontrado",
                    "ALQUILER_NOT_FOUND",
                    "No se encontró un alquiler con el ID proporcionado: " + idAlquiler);
        }
        return alquiler;
    }

    public List<Alquiler> getAllAlquileres() {
        return alquilerRepository.findAll();
    }

    public void deleteAlquilerById(String idAlquiler) {
        alquilerRepository.deleteById(idAlquiler);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void actualizarEstadosDeAlquileres() {
        LocalDateTime ahora = LocalDateTime.now();
        int contadorRetrasados = 0;
        int contadorNoDevueltos = 0;
        int contadorDanados = 0;
        int contadorIncidentes = 0;

        List<Alquiler> alquileres = alquilerRepository.findAll();

        for (Alquiler alquiler : alquileres) {
            Optional<Reserva> reservaOpt = reservaService.getReservaById(alquiler.getIdReserva());
            if (reservaOpt.isEmpty())
                continue;

            Reserva reserva = reservaOpt.get();

            switch (alquiler.getEstado()) {
                case EN_CURSO:
                    if (reserva.getFechaFin().isBefore(ahora)) {
                        alquiler.setEstado(EstadoAlquiler.RETRASADO);
                        alquilerRepository.save(alquiler);
                        contadorRetrasados++;
                    }
                    break;

                case RETRASADO:
                    if (alquiler.getFechaNovedad() != null &&
                            alquiler.getFechaNovedad().plusHours(3).isBefore(ahora)) {
                        alquiler.setEstado(EstadoAlquiler.NO_DEVUELTO);
                        alquiler.setFechaNovedad(ahora);
                        alquilerRepository.save(alquiler);
                        contadorNoDevueltos++;
                    }
                    break;

                case CONFIRMADO:
                    if (reserva.getFechaInicio().isBefore(ahora)) {
                        alquiler.setEstado(EstadoAlquiler.EN_CURSO);
                        alquiler.setFechaNovedad(ahora);
                        alquilerRepository.save(alquiler);
                    }
                    break;

                case VEHICULO_DANADO:
                    contadorDanados++;
                    break;

                case INCIDENTE_GRAVE:
                    contadorIncidentes++;
                    break;
                default:
                    continue;
            }
        }

        schedulerLogger.info("Actualización de estados de alquileres completada:");
        schedulerLogger.info("→ RETRASADOS: {}", contadorRetrasados);
        schedulerLogger.info("→ NO_DEVUELTO: {}", contadorNoDevueltos);
        schedulerLogger.info("→ VEHICULO_DANADO: {}", contadorDanados);
        schedulerLogger.info("→ INCIDENTE_GRAVE: {}", contadorIncidentes);
    }

    public Optional<Alquiler> getAlquilerByIdReserva(String idReserva) {
        return alquilerRepository.findByIdReserva(idReserva);
    }

    public List<Alquiler> getAlquileresByPropietarioIdAndEstado(String idPropietario, String estado) {
        EstadoAlquiler estadoEnum = EstadoAlquiler.valueOf(estado.toUpperCase());
        List<Alquiler> alquileres = getAllAlquileres();
        List<Alquiler> alquileresPropietario = new ArrayList<>();

        for (Alquiler a : alquileres) {
            Optional<Reserva> reservaOpt = reservaService.getReservaById(a.getIdReserva());
            if (reservaOpt.isEmpty())
                continue;

            Reserva r = reservaOpt.get();

            Optional<Vehiculo> vehiculoOpt = vehiculoService.getVehiculoById(r.getIdVehiculo());
            if (vehiculoOpt.isEmpty())
                continue;

            Vehiculo v = vehiculoOpt.get();

            if (v.getIdPropietario().equals(idPropietario) && a.getEstado() == estadoEnum) {
                alquileresPropietario.add(a);
            }
        }

        return alquileresPropietario;
    }

    public List<Alquiler> getAlquileresByPropietarioId(String idPropietario) {
        List<Alquiler> alquileres = getAllAlquileres();
        List<Alquiler> alquileresPropietario = new ArrayList<>();

        for (Alquiler a : alquileres) {
            Optional<Reserva> reservaOpt = reservaService.getReservaById(a.getIdReserva());
            if (reservaOpt.isEmpty())
                continue;

            Reserva r = reservaOpt.get();

            Optional<Vehiculo> vehiculoOpt = vehiculoService.getVehiculoById(r.getIdVehiculo());
            if (vehiculoOpt.isEmpty())
                continue;

            Vehiculo v = vehiculoOpt.get();

            if (v.getIdPropietario().equals(idPropietario)) {
                alquileresPropietario.add(a);
            }
        }

        return alquileresPropietario;
    }

    public Alquiler actualizarEstadoAlquiler(String idAlquiler, String nuevoEstado) {
        EstadoAlquiler estadoEnum;
        try {
            estadoEnum = EstadoAlquiler.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(
                    "Estado de alquiler inválido",
                    "INVALID_ESTADO",
                    "El estado '" + nuevoEstado + "' no es válido");
        }

        Optional<Alquiler> alquilerOpt = alquilerRepository.findById(idAlquiler);
        if (alquilerOpt.isEmpty()) {
            throw new InvalidInputException(
                    "Alquiler no encontrado",
                    "ALQUILER_NOT_FOUND",
                    "No se encontró un alquiler con el ID: " + idAlquiler);
        }

        Alquiler alquiler = alquilerOpt.get();
        alquiler.setEstado(estadoEnum);
        alquiler.setFechaNovedad(LocalDateTime.now());

        return alquilerRepository.save(alquiler);
    }

    public List<Alquiler> getAlquileresByIdCliente(String idCliente) {
        List<Alquiler> alquileres = alquilerRepository.findAll();
        List<Alquiler> resultado = new ArrayList<>();

        for (Alquiler alquiler : alquileres) {
            Optional<Reserva> reservaOpt = reservaService.getReservaById(alquiler.getIdReserva());
            if (reservaOpt.isPresent()) {
                Reserva reserva = reservaOpt.get();
                if (reserva.getIdCliente().equals(idCliente)) {
                    resultado.add(alquiler);
                }
            }
        }

        return resultado;
    }

    public List<Alquiler> getAlquileresActivosByIdCliente(String idCliente) {
        List<Alquiler> alquileres = alquilerRepository.findAll();
        List<Alquiler> resultado = new ArrayList<>();

        for (Alquiler alquiler : alquileres) {
            Optional<Reserva> reservaOpt = reservaService.getReservaById(alquiler.getIdReserva());
            if (reservaOpt.isPresent()) {
                Reserva reserva = reservaOpt.get();
                if (reserva.getIdCliente().equals(idCliente)) {
                    EstadoAlquiler estado = alquiler.getEstado();
                    if (estado == EstadoAlquiler.CONFIRMADO || estado == EstadoAlquiler.EN_CURSO) {
                        resultado.add(alquiler);
                    }
                }
            }
        }

        return resultado;
    }

}
