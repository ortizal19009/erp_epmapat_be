package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PreemisionReporteDTO {
    private Long idemision;
    private String emision;
    private int cuentas;
    private long m3;
    private BigDecimal valor = BigDecimal.ZERO;
    private int consumosNegativos;
    private List<Ruta> rutas = new ArrayList<>();

    @Data
    public static class Ruta {
        private Long idruta;
        private String codigo;
        private String ruta;
        private int cuentas;
        private long m3;
        private BigDecimal valor = BigDecimal.ZERO;
        private List<Cuenta> detalle = new ArrayList<>();
    }

    @Data
    public static class Cuenta {
        private Long cuenta;
        private String abonado;
        private String categoria;
        private Float anterior;
        private Float actual;
        private int m3;
        private BigDecimal valor;
        private String observacion;
    }
}
