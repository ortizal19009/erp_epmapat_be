package com.epmapat.erp_epmapat.DTO;

import java.util.List;
import lombok.Data;

@Data
public class SmartSyncRequestDto {
    private Long idusuario;
    private Long idemision;
    private List<String> modulos;
}
