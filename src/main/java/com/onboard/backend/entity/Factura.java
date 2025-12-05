package com.onboard.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Document(collection = "facturas")
public class Factura {

    @Id
    private String idFactura;
    private String idReserva;
    private BigDecimal total;
    private LocalDate fechaEmision;
    private String razon;
    private String estadoPago;
    private BigDecimal impuesto;

    public Factura() {
    }

    public Factura(String idFactura, String idReserva, BigDecimal total, LocalDate fechaEmision, String razon,
            String estadoPago) {
        this.idFactura = idFactura;
        this.idReserva = idReserva;
        this.total = total.setScale(2, RoundingMode.HALF_UP);
        this.fechaEmision = fechaEmision;
        this.razon = razon;
        this.estadoPago = estadoPago;
        this.impuesto = total.multiply(new BigDecimal("0.004")).setScale(2, RoundingMode.HALF_UP);
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal subtotal) {
        this.impuesto = subtotal.multiply(new BigDecimal("0.004")).setScale(2, RoundingMode.HALF_UP);
        this.total = subtotal.add(this.impuesto).setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }
}
