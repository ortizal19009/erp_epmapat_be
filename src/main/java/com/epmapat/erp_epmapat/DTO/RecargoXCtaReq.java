package com.epmapat.erp_epmapat.DTO;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class RecargoXCtaReq {
    private Long idabonado;
    private Long idemision;
    private Long idrubro;
    private int tipo; // 1 notif, 2 insp
    private String observacion;
    private Long usucrea;
    private Long usuresp;
    private Timestamp fecha;
}