package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvenioAuditDTO {
    private Long idconvenio;
    private String nroautorizacion;
    private String referencia;
    private Integer estado;
    private Integer nroconvenio;
    private BigDecimal totalconvenio;
    private Short cuotas;
    private BigDecimal cuotainicial;
    private BigDecimal pagomensual;
    private BigDecimal cuotafinal;
    private String observaciones;
    private Long usuarioeliminacion;
    private LocalDate fechaeliminacion;
    private String razoneliminacion;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private Timestamp fecmodi;
    private Long idabonado;
}
