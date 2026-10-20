package com.epmapat.erp_epmapat.nc;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
@RestControllerAdvice(assignableTypes=NcEndosoApi.class)
public class NcEndosoErrors {
 @ExceptionHandler(ResponseStatusException.class)
 public ResponseEntity<?> status(ResponseStatusException ex){return ResponseEntity.status(ex.getStatus()).body(Map.of("message",ex.getReason()==null?"Operacion no permitida":ex.getReason()));}
 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<?> invalid(IllegalArgumentException ex){return ResponseEntity.badRequest().body(Map.of("message","Parametros invalidos; revise fechas, importes e identificadores"));}
 @ExceptionHandler({DataIntegrityViolationException.class,PessimisticLockingFailureException.class})
 public ResponseEntity<?> conflict(RuntimeException ex){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Conflicto de integridad o concurrencia. Recargue antes de reintentar con la misma clave de operacion"));}
}
