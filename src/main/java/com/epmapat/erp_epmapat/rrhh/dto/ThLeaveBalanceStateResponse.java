package com.epmapat.erp_epmapat.rrhh.dto;

import java.util.List;
import lombok.Value;

@Value
public class ThLeaveBalanceStateResponse {
    Long idbalance;
    Long idpersonal;
    Integer anio;
    Boolean estado;
    Long version;
    List<ThLeaveHistoryResponse.Evento> eventos;
}
