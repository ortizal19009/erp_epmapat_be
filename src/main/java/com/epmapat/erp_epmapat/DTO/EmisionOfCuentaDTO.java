package com.epmapat.erp_epmapat.DTO;

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
}
