package com.epmapat.erp_epmapat.excepciones;

import java.net.SocketTimeoutException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.epmapat.erp_epmapat.controlador.TrackingController;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(assignableTypes = TrackingController.class)
public class TrackingApiExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleIncompleteBody(HttpMessageNotReadableException exception,
            HttpServletResponse response) {
        boolean timeout = false;
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof SocketTimeoutException) {
                timeout = true;
                break;
            }
        }
        log.warn("Peticion GPS no recibida completamente: {}", timeout ? "timeout de lectura" : "JSON invalido");
        // Tomcat puede haber cerrado la respuesta al agotar el tiempo de lectura.
        if (response.isCommitted()) return null;
        response.setStatus(timeout ? HttpServletResponse.SC_REQUEST_TIMEOUT : HttpServletResponse.SC_BAD_REQUEST);
        return Map.of("message", timeout
                ? "No se recibio el lote GPS completo. Reintente conservando los datos pendientes."
                : "El cuerpo de la peticion GPS no es un JSON valido.");
    }
}
