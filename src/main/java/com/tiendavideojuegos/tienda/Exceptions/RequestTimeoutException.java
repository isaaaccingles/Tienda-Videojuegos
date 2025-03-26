package com.tiendavideojuegos.tienda.Exceptions;

public class RequestTimeoutException extends RuntimeException {
    public RequestTimeoutException(String message) {
        super(message);
    }
}
