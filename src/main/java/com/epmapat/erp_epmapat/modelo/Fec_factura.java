package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fec_factura")
public class Fec_factura implements Serializable {
   @Id
   private Long idfactura;
   private String claveacceso;
   private String secuencial;
   private String xmlautorizado;
   private String errores;
   private String estado;
   private String establecimiento;
   private String puntoemision;
   private String direccionestablecimiento;
   private LocalDateTime fechaemision;
   private String tipoidentificacioncomprador;
   private String guiaremision;
   private String razonsocialcomprador;
   private String identificacioncomprador;
   private String direccioncomprador;
   private String telefonocomprador;
   private String emailcomprador;
   private String concepto;
   private String referencia;
   private String recaudador;
   private Integer intentosAutorizacion;
   private LocalDateTime fechaUltimoIntento;
   private LocalDateTime fechaAutorizacion;
   private Boolean mailEnviado;
   private Integer mailIntentos;
   private String mailError;
   private String emailEstado;
   private LocalDateTime fechaReenvio;

public Fec_factura orElseThrow(Object object) {
    throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
}

}
