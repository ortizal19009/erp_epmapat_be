package com.epmapat.erp_epmapat.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AbonadoMobileUpdateDto {
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
    private Long idcategoria_categorias;
    private Long idruta_rutas;
    private Long idcliente_clientes;
    private Long idubicacionm_ubicacionm;
    private Long idtipopago_tipopago;
    private Long idestadom_estadom;
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
    private String fotocasaPath;
    private String fotomedidorPath;
}
