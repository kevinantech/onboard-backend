package com.onboard.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "particulares")
public class Particular {

    @Id
    private String id; 

    private String idUsuario; 

    private String licenciaConduccion;

    public Particular() {}

    public Particular(String idUsuario, String licenciaConduccion) {
        this.idUsuario = idUsuario;
        this.licenciaConduccion = licenciaConduccion;
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

    public String getLicenciaConduccion() {
        return licenciaConduccion;
    }

    public void setLicenciaConduccion(String licenciaConduccion) {
        this.licenciaConduccion = licenciaConduccion;
    }
    
}
