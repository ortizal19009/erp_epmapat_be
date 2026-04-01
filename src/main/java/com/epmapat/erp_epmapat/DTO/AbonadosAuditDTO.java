package com.epmapat.erp_epmapat.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbonadosAuditDTO {
    private Long idabonado;
    private String nromedidor;
    private Long lecturainicial;
    private Long estado;
    private LocalDate fechainstalacion;
    private String marca;
    private Long secuencia;
    private String direccionubicacion;
    private String localizacion;
    private String observacion;
    private String departamento;
    private String piso;
    private Long idresponsable;
    private Long idcategoria;
    private Long idruta;
    private Long idcliente;
    private Long idubicacionm;
    private Long idtipopago;
    private Long idestadom;
    private Long medidorprincipal;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
    private Boolean adultomayor;
    private Boolean municipio;
    private Boolean swalcantarillado;
    private Long promedio;
    private String geolocalizacion;
    private Boolean swbasura;
    private String fotoPath;
}
