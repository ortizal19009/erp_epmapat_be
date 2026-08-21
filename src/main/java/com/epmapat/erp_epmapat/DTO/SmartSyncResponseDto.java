package com.epmapat.erp_epmapat.DTO;

import java.util.List;

import com.epmapat.erp_epmapat.interfaces.mobile.NovedadesMobile;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Categorias;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.modelo.Nacionalidad;
import com.epmapat.erp_epmapat.modelo.Pliego24;
import com.epmapat.erp_epmapat.modelo.Rutas;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmartSyncResponseDto {
    private List<LecturaDto> lecturas;
    private List<Abonados> abonados;
    private List<Clientes> clientes;
    private List<Rutas> rutas;
    private List<Categorias> categorias;
    private List<NovedadesMobile> novedades;
    private List<Nacionalidad> nacionalidades;
    private List<Pliego24Dto> pliegos;
}
