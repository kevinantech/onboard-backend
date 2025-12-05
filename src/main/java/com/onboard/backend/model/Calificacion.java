package com.onboard.backend.model;

public class Calificacion {
    private float valor;
    private String comentario;

    public Calificacion(float valor, String comentario) {
        this.valor = valor;
        this.comentario = comentario;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
