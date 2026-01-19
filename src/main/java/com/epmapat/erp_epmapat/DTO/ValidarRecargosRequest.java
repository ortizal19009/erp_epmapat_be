package com.epmapat.erp_epmapat.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ValidarRecargosRequest {
    private Long idemision;
    private LocalDate fecha; // base para anual/mensual
    private List<Item> items;

    @Data
    public static class Item {
        private Long idabonado;
        private Integer tipo; // 1 o 2
    }
}
