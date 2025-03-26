package com.tiendavideojuegos.tienda.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja excepciones de validación (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<String> errorMessages = result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", errorMessages);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400
    }

    // Maneja excepciones no autorizadas (401)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401
    }

    // Maneja excepciones de acceso prohibido (403)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "FORBIDDEN", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // 403
    }

    // Maneja excepciones de recurso no encontrado (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404
    }

    // Maneja excepciones de método no permitido (405)
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), "METHOD_NOT_ALLOWED", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED); // 405
    }

    // Maneja excepciones de not acceptable (406)
    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleNotAcceptableException(NotAcceptableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), "NOT_ACCEPTABLE", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE); // 406
    }

    // Maneja excepciones de timeout de solicitud (408)
    @ExceptionHandler(RequestTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleRequestTimeoutException(RequestTimeoutException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.REQUEST_TIMEOUT.value(), "REQUEST_TIMEOUT", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT); // 408
    }

    // Maneja excepciones de conflicto (409)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), "CONFLICT", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // 409
    }

    // Maneja excepciones de Unsupported Media Type (415)
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "UNSUPPORTED_MEDIA_TYPE", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE); // 415
    }

    // Maneja excepciones de Too Many Requests (429)
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(TooManyRequestsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS); // 429
    }

    // Maneja excepciones de error interno del servidor (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR", "Ocurrió un error interno. Intente nuevamente.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    // Maneja excepciones de Gateway Timeout (504)
    @ExceptionHandler(GatewayTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleGatewayTimeoutException(GatewayTimeoutException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.GATEWAY_TIMEOUT.value(), "GATEWAY_TIMEOUT", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT); // 504
    }


    // Clase para manejar la respuesta del error
    public static class ErrorResponse {
        private int statusCode;
        private String errorCode;
        private Object message;

        public ErrorResponse(int statusCode, String errorCode, Object message) {
            this.statusCode = statusCode;
            this.errorCode = errorCode;
            this.message = message;
        }

        // Getters y setters
        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }
    }
}
