package com.epmapat.erp_epmapat.gestiondocumental.dto.catalogo;

import lombok.Data;

@Data
public class CatalogoRequests {
    @Data
    public static class EntidadRequest {
        private String codigo;
        private String nombre;
        private Boolean active;
    }

    @Data
    public static class DependenciaRequest {
        private String entity_code;
        private String codigo;
        private String nombre;
        private String padre_id;
        private Boolean active;
    }

    @Data
    public static class TipoDocumentoRequest {
        private String entity_code;
        private String codigo;
        private String nombre;
        private String flujo;
        private Boolean active;
    }
}
