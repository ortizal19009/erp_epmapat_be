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
    private Date fechalectura;
    private Float lecturaanterior;
    private Float lecturaactual;
    private Float lecturadigitada;
    private Integer mesesmulta;
    private String observaciones;
    private Long idnovedad;
    private Long idemision;
    private Long idabonado;
    private Long idresponsable;
    private Long usuariolectura;
    private Long idcategoria;
    private Long idfactura;
    private Long usumodi;
    private Date fecmodi;
    private BigDecimal total1;
    private BigDecimal total31;
    private BigDecimal total32;
}
