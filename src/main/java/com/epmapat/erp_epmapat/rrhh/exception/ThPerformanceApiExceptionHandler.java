package com.epmapat.erp_epmapat.rrhh.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.controlador.ThPerformanceApi;

@RestControllerAdvice(assignableTypes = ThPerformanceApi.class)
public class ThPerformanceApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> status(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("message", e.getReason() == null ? "Operación no permitida" : e.getReason()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalid(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Revise los campos obligatorios, sus longitudes y el puntaje de 0 a 100 con hasta dos decimales."));
    }
}
