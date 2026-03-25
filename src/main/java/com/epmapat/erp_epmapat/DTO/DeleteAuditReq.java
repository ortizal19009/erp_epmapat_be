package com.epmapat.erp_epmapat.DTO;

import lombok.Data;

@Data
public class DeleteAuditReq {
    private Long usumodi;
    private String observacion;
    private String tipo; // "ELIMINACION" o "MODIFICACION"
}
