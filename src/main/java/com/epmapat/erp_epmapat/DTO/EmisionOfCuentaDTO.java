package com.epmapat.erp_epmapat.DTO;

import java.util.Optional;

import com.epmapat.erp_epmapat.modelo.Categorias;
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
    Pliego24 pliego24;
    Categorias categorias;
}

