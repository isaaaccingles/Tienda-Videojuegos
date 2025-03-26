package com.tiendavideojuegos.tienda.Exceptions;

public class GatewayTimeoutException extends RuntimeException {
    public GatewayTimeoutException(String message) {
        super(message);
    }
}
