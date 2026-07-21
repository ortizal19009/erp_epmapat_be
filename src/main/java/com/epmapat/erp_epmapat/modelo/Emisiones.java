package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emisiones")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Emisiones implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idemision;
    String emision;
    Integer estado;
    String observaciones;
    Long usuariocierre;
    @Column(name = "fechacierre")
    private ZonedDateTime fechacierre;
    Long m3;
    Long usucrea;
    Date feccrea;
    Long usumodi;
    Date fecmodi;
    Long iddocumentoAnulacion;
    String documentoAnulacion;
    String referenciaDocumentoAnulacion;
    String motivoAnulacion;
    Long usuarioAnulacion;
    @Column(name = "fechaanulacion")
    private ZonedDateTime fechaanulacion;
    @Transient
    private Long totalLecturas;
    @Transient
    private Long lecturasCargadas;

}
