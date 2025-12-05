package com.onboard.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.onboard.backend.model.TipoIdentificacion;

@Document(collection = "empresas")
public class Empresa {

    @Id
    private String id;

    private String idUsuario;

    private String representante;
    private String documentoRepresentante;
    private TipoIdentificacion tipoDocumentoRepresentante;

    public Empresa() {}

    public Empresa(String idUsuario, String representante, String documentoRepresentante, TipoIdentificacion tipoDocumentoRepresentante) {
        this.idUsuario = idUsuario;
        this.representante = representante;
        this.documentoRepresentante = documentoRepresentante;
        this.tipoDocumentoRepresentante = tipoDocumentoRepresentante;
    }

    public String getId() {
        return id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public String getDocumentoRepresentante() {
        return documentoRepresentante;
    }

    public void setDocumentoRepresentante(String documentoRepresentante) {
        this.documentoRepresentante = documentoRepresentante;
    }

    public TipoIdentificacion getTipoDocumentoRepresentante() {
        return tipoDocumentoRepresentante;
    }

    public void setTipoDocumentoRepresentante(TipoIdentificacion tipoDocumentoRepresentante) {
        this.tipoDocumentoRepresentante = tipoDocumentoRepresentante;
    }
}
