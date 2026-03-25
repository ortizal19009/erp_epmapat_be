package com.epmapat.erp_epmapat.DTO;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class RecargoXCtaUpdateReq {
    // Campos de actualización
    private Long idabonado;
    private Long idemision;
    private Long idrubro;
    private Integer tipo; // 1 notif, 2 insp
    private String observacion;
    private Long usucrea;
    private Long usuresp;
    private Timestamp fecha;

    // Campos de auditoría
    private Long usumodi;
    private String observacionAuditoria;
    private String tipoAuditoria;
}