package com.epmapat.erp_epmapat.excepciones;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
public class MobileSyncValidationException extends ResponseStatusException {
    private final String code;
    public MobileSyncValidationException(String code, String message) { super(HttpStatus.BAD_REQUEST, message); this.code=code; }
    public String getCode() { return code; }
}
