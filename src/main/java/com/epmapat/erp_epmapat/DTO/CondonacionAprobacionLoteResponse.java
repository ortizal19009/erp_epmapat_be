package com.epmapat.erp_epmapat.DTO;

import java.util.List;

public class CondonacionAprobacionLoteResponse {
    private final List<CondonacionResponse> aprobadas;
    private final List<String> omitidas;

    public CondonacionAprobacionLoteResponse(List<CondonacionResponse> aprobadas, List<String> omitidas) {
        this.aprobadas = aprobadas;
        this.omitidas = omitidas;
    }

    public List<CondonacionResponse> getAprobadas() { return aprobadas; }
    public List<String> getOmitidas() { return omitidas; }
}
