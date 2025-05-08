package com.epmapat.erp_epmapat.sri.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fec_factura_detalles")
public class FacturaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacturadetalle;
    
    @ManyToOne
    @JoinColumn(name = "idfactura")
    private Factura factura;
    
    private String codigoprincipal;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal preciounitario;
    private BigDecimal descuento;
    
    @OneToMany(mappedBy = "detalle", cascade = CascadeType.ALL)
    private List<FacturaDetalleImpuesto> impuestos = new ArrayList<>();

    public Object getPreciototalsinimpuesto() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPreciototalsinimpuesto'");
    }
    
    // Getters y Setters
}