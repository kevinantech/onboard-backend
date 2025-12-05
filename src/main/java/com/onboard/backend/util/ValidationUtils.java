package com.onboard.backend.util;

import java.util.regex.Pattern;

import com.onboard.backend.exception.InvalidInputException;
import com.onboard.backend.model.TipoIdentificacion;

import java.time.Year;

public class ValidationUtils {

    public static final String[] VEHICULOS_PERMITIDOS = {
            "Car", "Motorcycle", "Bus", "Boat", "Truck",
            "SUV", "Van", "Pickup", "Bicycle", "ATV", "Jet Ski"
    };

    public static final String[] TIPOS_TRANSMISION_PERMITIDOS = {
            "Automatic", "Manual", "Semi-automatic"
    };

    public static final String[] TIPOS_COMBUSTIBLE_PERMITIDOS = {
            "Gasoline", "Diesel", "Electric", "Hybrid", "Natural Gas", "Hydrogen"
    };

    public static final String[] TIPOS_TERRENO_PERMITIDOS = {
            "Urban", "Rural", "Mixed", "Highway", "Off-road"
    };

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern CEDULA_PATTERN = Pattern.compile("^\\d{6,10}$");
    private static final Pattern PASAPORTE_PATTERN = Pattern.compile("^[A-Z]{1,2}\\d{6,8}$");
    private static final Pattern NIT_PATTERN = Pattern.compile("^\\d{6,10}-\\d$");
    private static final Pattern CUENTA_BANCARIA_PATTERN = Pattern.compile("^\\d{10,20}$");
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,100}$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\+?\\d{7,15}$");
    private static final Pattern LICENCIA_CONDUCCION_PATTERN = Pattern.compile("^\\d{6,12}$");

    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z]{3}\\d{3,4}$");
    private static final Pattern MARCA_PATTERN = Pattern.compile("^.{2,30}$");
    private static final Pattern MODELO_PATTERN = Pattern.compile("^.{1,30}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://.*$");
    private static final Pattern DESCRIPCION_PATTERN = Pattern.compile("^.{10,500}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static void validarDocumento(String identificacion, TipoIdentificacion tipo) {
        if (identificacion == null || tipo == null) {
            throw new InvalidInputException(
                    "Identification or type cannot be null",
                    "NULL_ID_OR_TYPE",
                    "Both identification and type are required.");
        }

        switch (tipo) {
            case CC:
            case CE:
                if (!CEDULA_PATTERN.matcher(identificacion).matches()) {
                    throw new InvalidInputException(
                            "Invalid cedula format",
                            "INVALID_ID_FORMAT",
                            "For CC or CE, ID must be 6 to 10 digits. Example: 1025487632");
                }
                break;
            case PASAPORTE:
                if (!PASAPORTE_PATTERN.matcher(identificacion).matches()) {
                    throw new InvalidInputException(
                            "Invalid passport format",
                            "INVALID_PASSPORT_FORMAT",
                            "Passport format is invalid. Example: AB123456");
                }
                break;
            case NIT:
                if (!NIT_PATTERN.matcher(identificacion).matches()) {
                    throw new InvalidInputException(
                            "Invalid NIT format",
                            "INVALID_NIT_FORMAT",
                            "NIT must follow the format 123456789-0. Example: 900123456-7");
                }
                break;
            default:
                throw new InvalidInputException(
                        "Unknown identification type",
                        "UNKNOWN_ID_TYPE",
                        "The identification type is not recognized.");
        }
    }

    public static boolean isValidRepresentante(String representante) {
        return representante != null && NOMBRE_PATTERN.matcher(representante).matches();
    }

    public static boolean isValidCuentaBancaria(String cuentaBancaria) {
        return cuentaBancaria != null && CUENTA_BANCARIA_PATTERN.matcher(cuentaBancaria).matches();
    }

    public static boolean isValidNombre(String nombre) {
        return nombre != null && NOMBRE_PATTERN.matcher(nombre).matches();
    }

    public static boolean isValidTelefono(String telefono) {
        return telefono != null && TELEFONO_PATTERN.matcher(telefono).matches();
    }

    public static boolean isValidDireccion(String direccion) {
        return direccion != null && direccion.length() >= 5 && direccion.length() <= 150;
    }

    public static boolean isValidPlaca(String placa) {
        return placa != null && PLACA_PATTERN.matcher(placa).matches();
    }

    public static boolean isValidMarca(String marca) {
        return marca != null && MARCA_PATTERN.matcher(marca).matches();
    }

    public static boolean isValidModelo(String modelo) {
        return modelo != null && MODELO_PATTERN.matcher(modelo).matches();
    }

    public static boolean isValidAnio(int anio) {
        int anioActual = Year.now().getValue();
        return anio >= 1960 && anio <= anioActual + 1;
    }

    public static boolean isValidCapacidadPasajeros(int capacidad) {
        return capacidad > 0 && capacidad <= 100;
    }

    public static boolean isValidKilometraje(float km) {
        return km >= 0;
    }

    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    public static boolean isValidTipo(String tipo, String[] valoresPermitidos) {
        if (tipo == null)
            return false;
        for (String permitido : valoresPermitidos) {
            if (permitido.equalsIgnoreCase(tipo))
                return true;
        }
        return false;
    }

    public static boolean isValidTransmision(String tipo) {
        return isValidTipo(tipo, TIPOS_TRANSMISION_PERMITIDOS);
    }

    public static boolean isValidCombustible(String tipo) {
        return isValidTipo(tipo, TIPOS_COMBUSTIBLE_PERMITIDOS);
    }

    public static boolean isValidTipoVehiculo(String tipo) {
        return isValidTipo(tipo, VEHICULOS_PERMITIDOS);
    }

    public static boolean isValidTipoTerreno(String tipo) {
        return isValidTipo(tipo, TIPOS_TERRENO_PERMITIDOS);
    }

    public static boolean isValidDescripcion(String descripcion) {
        return descripcion != null && DESCRIPCION_PATTERN.matcher(descripcion).matches();
    }

    public static boolean isValidLicenciaConduccion(String licencia) {
        return licencia != null && LICENCIA_CONDUCCION_PATTERN.matcher(licencia.trim()).matches();
    }

    public static void validarCoordenadasCartagena(String coordenadas) {
        if (coordenadas == null || !coordenadas.contains(",")) {
            throw new InvalidInputException(
                    "Invalid coordinate format",
                    "INVALID_COORDINATE_FORMAT",
                    "Coordinates must be in the format 'latitude,longitude'. Example: 10.4,-75.5");
        }

        String[] partes = coordenadas.split(",");
        if (partes.length != 2) {
            throw new InvalidInputException(
                    "Invalid coordinate parts",
                    "INVALID_COORDINATE_PARTS",
                    "Coordinates must contain latitude and longitude.");
        }

        try {
            double lat = Double.parseDouble(partes[0].trim());
            double lon = Double.parseDouble(partes[1].trim());


            if (lat < 10.3 || lat > 10.5 || lon < -75.6 || lon > -75.4) {
                throw new InvalidInputException(
                        "Coordinates outside Cartagena",
                        "OUT_OF_CARTAGENA",
                        "The coordinates must be located within Cartagena, Bolívar.");
            }

        } catch (NumberFormatException e) {
            throw new InvalidInputException(
                    "Coordinates must be numeric",
                    "NON_NUMERIC_COORDINATES",
                    "Latitude and longitude must be numeric values.");
        }
    }

}
