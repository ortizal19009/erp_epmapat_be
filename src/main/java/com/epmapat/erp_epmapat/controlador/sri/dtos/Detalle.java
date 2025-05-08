package com.epmapat.erp_epmapat.controlador.sri.dtos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.epmapat.erp_epmapat.controlador.sri.models.Factura;
import com.epmapat.erp_epmapat.controlador.sri.models.FacturaDetalleImpuesto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Detalle {
    private Long idfacturadetalle;
    private Factura factura;
    private String codigoPrincipal;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal preciounitario;
    private BigDecimal descuento;
    
    private List<FacturaDetalleImpuesto> impuestos = new ArrayList<>();
}
