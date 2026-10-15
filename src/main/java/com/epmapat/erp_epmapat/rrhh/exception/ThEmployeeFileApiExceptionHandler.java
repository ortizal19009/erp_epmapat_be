package com.epmapat.erp_epmapat.rrhh.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import com.epmapat.erp_epmapat.rrhh.controlador.ThEmployeeFileApi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(assignableTypes = ThEmployeeFileApi.class)
public class ThEmployeeFileApiExceptionHandler {
    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
        org.springframework.web.multipart.support.MissingServletRequestPartException.class,
        org.springframework.web.HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<?> invalid(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("message","Revise el archivo adjunto y el formato de los datos del documento."));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> status(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("message",e.getReason() == null ? "Operación no permitida" : e.getReason()));
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> size(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(413).body(Map.of("message","El adjunto supera el límite permitido."));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unexpected(Exception e) {
        log.error("Error en expediente digital",e);
        return ResponseEntity.internalServerError().body(Map.of("message","No se pudo completar la operación. Revise el almacenamiento y el servicio de expedientes."));
    }
}
