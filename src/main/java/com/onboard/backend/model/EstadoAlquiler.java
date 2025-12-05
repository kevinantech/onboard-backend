package com.onboard.backend.model;

public enum EstadoAlquiler {
    CONFIRMADO,           // Aprobado, vehículo no entregado aún
    EN_CURSO,             // Vehículo entregado al cliente
    FINALIZADO,           // Vehículo devuelto correctamente
    RETRASADO,            // Cliente no ha devuelto el vehículo a tiempo
    NO_DEVUELTO,          // Cliente nunca devolvió el vehículo (se asume pérdida o robo)
    VEHICULO_DANADO,      // Vehículo devuelto con daños significativos
    INCIDENTE_GRAVE       // Se reportó un accidente o situación legal importante durante el alquiler
}