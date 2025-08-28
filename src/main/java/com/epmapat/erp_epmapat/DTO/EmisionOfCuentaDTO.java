package com.epmapat.erp_epmapat.DTO;

import com.epmapat.erp_epmapat.modelo.Categorias;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.modelo.Pliego24;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmisionOfCuentaDTO {
    Long cuenta;
    Long idfactura;
    int m3;
    int categoria;
    boolean swMunicipio;
    boolean swAdultoMayor;
    boolean swAguapotable;
    Pliego24 pliego24;
    Categorias categorias;
    Facturas factura;
}

