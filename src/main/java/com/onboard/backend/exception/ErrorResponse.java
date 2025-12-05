package com.onboard.backend.exception;

public class ErrorResponse {
    private String codigo;
    private String mensaje;
    private String detalle;

    public ErrorResponse(String codigo, String mensaje, String detalle) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.detalle = detalle;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
