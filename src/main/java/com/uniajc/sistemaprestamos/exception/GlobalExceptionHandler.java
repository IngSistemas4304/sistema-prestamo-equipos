package com.uniajc.sistemaprestamos.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarErrorTipoParametro(
            MethodArgumentTypeMismatchException ex) {

        Map<String, String> respuesta = new HashMap<>();

        respuesta.put("error", "Parametro invalido.");
        respuesta.put("detalle", "El valor enviado no tiene el formato esperado.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErrorGeneral(Exception ex) {

        Map<String, String> respuesta = new HashMap<>();

        respuesta.put("error", "Error interno del servidor.");
        respuesta.put("detalle", "Ocurrio un problema al procesar la solicitud.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(respuesta);
    }
}