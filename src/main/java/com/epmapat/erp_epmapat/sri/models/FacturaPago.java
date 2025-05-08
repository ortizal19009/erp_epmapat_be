package com.epmapat.erp_epmapat.sri.models;

import java.math.BigDecimal;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "fec_factura_pagos")
public class FacturaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacturapagos;
    
    @ManyToOne
    @JoinColumn(name = "idfactura")
    private Factura factura;
    
    private String formapago;
    private BigDecimal total;
    private Integer plazo;
    private String unidadtiempo;
    
    // Getters y Setters
}
