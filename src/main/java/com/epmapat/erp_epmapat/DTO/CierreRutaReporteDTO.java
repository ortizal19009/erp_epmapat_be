package com.epmapat.erp_epmapat.DTO;

import java.util.Date;
import java.util.List;

import com.epmapat.erp_epmapat.interfaces.CierreRutaCategoria;
import com.epmapat.erp_epmapat.interfaces.CierreRutaMultaDetalle;
import com.epmapat.erp_epmapat.interfaces.CierreRutaResumenTotales;
import com.epmapat.erp_epmapat.interfaces.CierreRutaRubroResumen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CierreRutaReporteDTO {
    private Long idrutaxemision;
    private Long idemision;
    private String emision;
    private Long idruta;
    private String ruta;
    private Date fechacierre;
    private CierreRutaResumenTotales totales;
    private List<CierreRutaCategoria> categorias;
    private List<CierreRutaRubroResumen> rubros;
    private List<CierreRutaMultaDetalle> multas;
}
