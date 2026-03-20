package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fec_retenciones")
public class Fec_retenciones implements Serializable {
   @Id
   private Long idretencion;
   private String claveacceso;
   private String secuencial;
   private String xmlautorizado;
   private String errores;
   private String estado;
   private String establecimiento;
   private String puntoemision;
   private String direccionestablecimiento;
   private LocalDate fechaemision;

   private String tipoidentificacionsujetoretenid;

   private String razonsocialsujetoretenido;
   private String identificacionsujetoretenido;
   private String periodofiscal;
   private String telefonosujetoretenido;
   private String emailsujetoretenido;
}
