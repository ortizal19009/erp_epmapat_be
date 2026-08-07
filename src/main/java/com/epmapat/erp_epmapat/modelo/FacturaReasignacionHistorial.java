package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "factura_reasignacion_historial")
public class FacturaReasignacionHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idreasignacion;
    private Long idfactura;
    private Long idusuarioaccion;
    private Long idrecaudador;
    private Long idcaja;
    private Long idrecaudaxcaja;

    @Column(length = 30)
    private String secuencialanterior;

    @Column(length = 30)
    private String secuencialnuevo;

    @Column(length = 30)
    private String nrofacturaanterior;

    @Column(length = 30)
    private String nrofacturanuevo;

    @Column(length = 60)
    private String claveaccesoanterior;

    @Column(length = 60)
    private String claveaccesonueva;

    private String estadoanterior;
    private String estadonuevo;

    @Column(columnDefinition = "text")
    private String xmlanterior;

    @Column(columnDefinition = "text")
    private String xmlnuevo;

    @Column(columnDefinition = "text")
    private String observacion;

    private LocalDateTime fechareasignacion;
}
