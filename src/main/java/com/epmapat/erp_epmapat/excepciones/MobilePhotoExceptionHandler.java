package com.epmapat.erp_epmapat.excepciones;

import com.epmapat.erp_epmapat.controlador.LecturasApi;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes=LecturasApi.class)
public class MobilePhotoExceptionHandler {
    private static final Logger log=LoggerFactory.getLogger(MobilePhotoExceptionHandler.class);
    @ExceptionHandler(PhotoStorageException.class)
    public ResponseEntity<Map<String,Object>> storageFailure(PhotoStorageException error) {
        Map<String,Object> body=new LinkedHashMap<>();
        body.put("errorCode",error.getCode()); body.put("retryable",error.isRetryable());
        body.put("source","PHOTO_STORAGE"); body.put("upstreamStatus",error.getUpstreamStatus());
        body.put("message","La foto se conserva en el dispositivo; revisa el almacenamiento del servidor.");
        log.warn("PHOTO_STORAGE_FAILED code={} upstreamStatus={} retryable={}",error.getCode(),error.getUpstreamStatus(),error.isRetryable());
        return ResponseEntity.status(error.isRetryable()?503:502).body(body);
    }
}
