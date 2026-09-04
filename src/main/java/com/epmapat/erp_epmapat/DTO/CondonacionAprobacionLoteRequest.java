package com.epmapat.erp_epmapat.DTO;

import java.util.List;

public class CondonacionAprobacionLoteRequest {
    private List<Long> ids;
    private String observacion;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
