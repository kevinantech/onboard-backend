package com.onboard.backend.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FormatUtils {

    public static String capitalizarNombre(String nombre) {
        if (nombre == null || nombre.isBlank())
            return nombre;

        return Arrays.stream(nombre.trim().split("\\s+"))
                .map(p -> p.substring(0, 1).toUpperCase() + p.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static String formatearCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase();
    }

    public static String limpiarCadena(String texto) {
        return texto == null ? null : texto.trim().replaceAll("\\s{2,}", " ");
    }

    public static String capitalizarFrase(String texto) {
        if (texto == null || texto.isBlank())
            return texto;
        texto = texto.trim();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    public static String formatPhoneNumber(String phone) {
        return phone == null ? null : phone.replaceAll("\\s+", "");
    }
}
