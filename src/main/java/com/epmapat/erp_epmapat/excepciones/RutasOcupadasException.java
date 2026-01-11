package com.epmapat.erp_epmapat.excepciones;

import java.util.List;

public class RutasOcupadasException extends RuntimeException {
    private final List<Long> ocupadas;

    public RutasOcupadasException(String message, List<Long> ocupadas) {
        super(message);
        this.ocupadas = ocupadas;
    }

    public List<Long> getOcupadas() {
        return ocupadas;
    }
}
