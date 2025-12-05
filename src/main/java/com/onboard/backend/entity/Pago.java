package com.onboard.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "pagos")
public class Pago {

    @Id
    private String idPago;
    private String idFactura;
    private LocalDate fechaPago;
    private String estadoPago;
    private String detalle;

    public Pago() {}

    public Pago(String idPago, String idFactura, LocalDate fechaPago, String estadoPago, String detalle) {
        this.idPago = idPago;
        this.idFactura = idFactura;
        this.fechaPago = fechaPago;
        this.estadoPago = estadoPago;
        this.detalle = detalle;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }


    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
