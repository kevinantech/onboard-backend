package com.onboard.backend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.onboard.backend.model.EstadoReserva;


@Document(collection = "reservas")
public class Reserva {

    @Id
    private String idReserva;
    private String idCliente;
    private String idVehiculo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String lugarEntregaYRecogida;
    private EstadoReserva estadoReserva;

    public Reserva() {
    }

    public Reserva(String idReserva, String idCliente, String idVehiculo, LocalDateTime fechaInicio,
                   LocalDateTime fechaFin, String lugarEntregaYRecogida, EstadoReserva estadoReserva) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.idVehiculo = idVehiculo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.lugarEntregaYRecogida = lugarEntregaYRecogida;
        this.estadoReserva = estadoReserva;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(String idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getLugarEntregaYRecogida() {
        return lugarEntregaYRecogida;
    }

    public void setLugarEntregaYRecogida(String lugarEntregaYRecogida) {
        this.lugarEntregaYRecogida = lugarEntregaYRecogida;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }
}
