package com.epmapat.erp_epmapat.controlador.sri.models;

import java.math.BigDecimal;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "fec_factura_detalles_impuestos")
public class FacturaDetalleImpuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacturadetalleimpuestos;
    
    @ManyToOne
    @JoinColumn(name = "idfacturadetalle")
    private FacturaDetalle detalle;
    
    private String codigoimpuesto;
    private String codigoporcentaje;
    private BigDecimal baseimponible;
    
    // Getters y Setters
}
