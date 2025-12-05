package com.onboard.backend.exception;

public class InvalidInputException extends RuntimeException {
    private final String errorCode;
    private final String detalle;


    public InvalidInputException(String message, String errorCode, String detalle) {
        super(message);
        this.errorCode = errorCode;
        this.detalle = detalle;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public String getDetalle() {
        return detalle;
    }
}
