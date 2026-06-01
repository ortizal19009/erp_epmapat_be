package com.epmapat.erp_epmapat.DTO;

import java.util.List;
import lombok.Data;
import lombok.Builder;
import com.epmapat.erp_epmapat.modelo.*;

@Data
@Builder
public class SmartSyncResponseDto {
    private List<LecturaDto> lecturas;
    private List<Abonados> abonados;
    private List<Clientes> clientes;
    private List<Rutas> rutas;
    private List<Categorias> categorias;
    private List<Novedades> novedades;
    private List<Nacionalidad> nacionalidades;
    private List<Pliego24> pliegos;
}
