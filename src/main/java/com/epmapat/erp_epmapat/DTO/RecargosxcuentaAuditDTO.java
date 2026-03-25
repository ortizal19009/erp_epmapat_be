package com.epmapat.erp_epmapat.DTO;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecargosxcuentaAuditDTO {
    private Long idrecargoxcuenta;
    private Long idabonado; // Solo el ID, no el objeto completo
    private Long idemision; // Solo el ID, no el objeto completo
    private Long idrubro;   // Solo el ID, no el objeto completo
    private int tipo;
    private String observacion;
    private Long usucrea;
    private Timestamp feccrea;
    private Long usumodi;
    private Timestamp fecmodi;
    private Long usuresp;
    private Timestamp fecha;
}