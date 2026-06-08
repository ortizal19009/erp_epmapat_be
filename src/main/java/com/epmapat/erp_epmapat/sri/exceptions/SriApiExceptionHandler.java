package com.epmapat.erp_epmapat.sri.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.epmapat.erp_epmapat.sri")
public class SriApiExceptionHandler {

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
      return buildResponse(HttpStatus.BAD_REQUEST, ex);
   }

   @ExceptionHandler(IllegalStateException.class)
   public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
      return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex);
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
      return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
   }

   private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, Exception ex) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("timestamp", LocalDateTime.now());
      body.put("status", status.value());
      body.put("error", status.getReasonPhrase());
      body.put("message", ex.getMessage());
      return ResponseEntity.status(status).body(body);
   }
}
