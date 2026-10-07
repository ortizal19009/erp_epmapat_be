package com.epmapat.erp_epmapat.rrhh.exception;

import java.util.Map;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.controlador.ThLeaveApi;

@RestControllerAdvice(assignableTypes = ThLeaveApi.class)
public class ThLeaveApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("status", ex.getStatus().value(),
                "message", ex.getReason() == null ? ex.getStatus().getReasonPhrase() : ex.getReason()));
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<Map<String, Object>> concurrency(ConcurrencyFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", 409,
                "message", "Otra operación está modificando la solicitud o el saldo. Actualice e intente nuevamente."));
    }
}
