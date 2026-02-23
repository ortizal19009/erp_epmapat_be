package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class LecturaDto {

    private Long idlectura;
    private Integer estado;
    private Long idrutaxemision;
    private Date fechaemision;
    private Float lecturaanterior;
    private Float lecturaactual;
    private Float lecturadigitada;
    private Integer mesesmulta;
    private String observaciones;
    private Long idnovedad;
    private Long idemision;
    private Long idabonado;
    private Long idresponsable;
    private Long idcategoria;
    private Long idfactura;
    private BigDecimal total1;
    private BigDecimal total31;
    private BigDecimal total32;
}