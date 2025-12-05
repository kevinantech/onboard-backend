package com.onboard.backend.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.onboard.backend.model.Calificacion;
import com.onboard.backend.model.EstadoOferta;
import com.onboard.backend.model.EstadoVerificacion;

@Document(collection = "vehiculos")
public class Vehiculo {

    @Id
    private String placa;

    private String tipoVehiculo;
    private String tipoTerreno;
    private String marca;
    private String modelo;
    private int anio;
    private int capacidadPasajeros;
    private String soat; // pdf url
    private String tecnomecanica; // pdf url
    private String antecedentes; // pdf url
    private String tipoTransmision;
    private String combustible;
    private float kilometraje;
    private String descripcion;
    private String idPropietario;
    private LocalDateTime fechaRegistro;
    private int cantidadAlquiler;
    private List<String> fotosUrls;
    private EstadoOferta estadoOferta;
    private float precioPorDia;
    private Map<String, Calificacion> calificaciones;

    public Vehiculo() {
    }

    public Vehiculo(String placa, String tipoVehiculo, String tipoTerreno, String marca, String modelo,
            int anio, int capacidadPasajeros, String soat, String tecnomecanica, String antecedentes,
            String tipoTransmision, String combustible, float kilometraje, String descripcion,
            String idPropietario, LocalDateTime fechaRegistro, int cantidadAlquiler,
            List<String> fotosUrls, EstadoOferta estadoOferta, float precioPorDia,
            EstadoVerificacion estadoVerificacion, Map<String, Calificacion> calificaciones) {

        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.tipoTerreno = tipoTerreno;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.capacidadPasajeros = capacidadPasajeros;
        this.soat = soat;
        this.tecnomecanica = tecnomecanica;
        this.antecedentes = antecedentes;
        this.tipoTransmision = tipoTransmision;
        this.combustible = combustible;
        this.kilometraje = kilometraje;
        this.descripcion = descripcion;
        this.idPropietario = idPropietario;
        this.fechaRegistro = fechaRegistro;
        this.cantidadAlquiler = cantidadAlquiler;
        this.fotosUrls = fotosUrls;
        this.estadoOferta = estadoOferta;
        this.precioPorDia = precioPorDia;
        //this.estadoVerificacion = estadoVerificacion;
        this.calificaciones = calificaciones;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getTipoTerreno() {
        return tipoTerreno;
    }

    public void setTipoTerreno(String tipoTerreno) {
        this.tipoTerreno = tipoTerreno;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(int capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public String getSoat() {
        return soat;
    }

    public void setSoat(String soat) {
        this.soat = soat;
    }

    public String getTecnomecanica() {
        return tecnomecanica;
    }

    public void setTecnomecanica(String tecnomecanica) {
        this.tecnomecanica = tecnomecanica;
    }

    public String getAntecedentes() {
        return antecedentes;
    }

    public void setAntecedentes(String antecedentes) {
        this.antecedentes = antecedentes;
    }

    public String getTipoTransmision() {
        return tipoTransmision;
    }

    public void setTipoTransmision(String tipoTransmision) {
        this.tipoTransmision = tipoTransmision;
    }

    public String getCombustible() {
        return combustible;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }

    public float getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(float kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(String idPropietario) {
        this.idPropietario = idPropietario;
    }

    public List<String> getFotosUrls() {
        return fotosUrls;
    }

    public void setFotosUrls(List<String> fotosUrls) {
        this.fotosUrls = fotosUrls;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getCantidadAlquiler() {
        return cantidadAlquiler;
    }

    public void setCantidadAlquiler(int cantidadAlquiler) {
        this.cantidadAlquiler = cantidadAlquiler;
    }

    public void setEstadoOferta(EstadoOferta estadoOferta) {
        this.estadoOferta = estadoOferta;
    }

    public void setPrecioPorDia(float precioPorDia) {
        this.precioPorDia = precioPorDia;
    }

    public EstadoOferta getEstadoOferta() {
        return estadoOferta;
    }

    public float getPrecioPorDia() {
        return precioPorDia;
    }

    /*public EstadoVerificacion getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(EstadoVerificacion estadoVerificacion) {
        this.estadoVerificacion = estadoVerificacion;
    }*/

    public Map<String, Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(Map<String, Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Vehiculo vehiculo = (Vehiculo) o;

        return placa != null ? placa.equals(vehiculo.placa) : vehiculo.placa == null;
    }

    @Override
    public int hashCode() {
        return placa != null ? placa.hashCode() : 0;
    }

}
