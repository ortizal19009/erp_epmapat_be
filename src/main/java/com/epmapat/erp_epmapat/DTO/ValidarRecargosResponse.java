package com.epmapat.erp_epmapat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ValidarRecargosResponse {
    private boolean ok = true;
    private List<Bloqueado> bloqueados = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class Bloqueado {
        private Long idabonado;
        private Integer tipo;
        private String motivo;
    }

    public void addBloqueado(Long idabonado, Integer tipo, String motivo) {
        ok = false;
        bloqueados.add(new Bloqueado(idabonado, tipo, motivo));
    }
}
