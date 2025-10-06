package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "facturamodificaciones")
public class Facturamodificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacturamodificaciones;
    private Long idfactura; 
    private String datosfactura; 
    private String detalle; 
    private LocalDate fechacrea;
    public Facturamodificaciones(){
        super(); 
    }
    public Facturamodificaciones(Long idfacturamodificaciones, Long idfactura, String datosfactura, String detalle,
            LocalDate fechacrea) {
        this.idfacturamodificaciones = idfacturamodificaciones;
        this.idfactura = idfactura;
        this.datosfactura = datosfactura;
        this.detalle = detalle;
        this.fechacrea = fechacrea;
    }
    public Long getIdfacturamodificaciones() {
        return idfacturamodificaciones;
    }
    public void setIdfacturamodificaciones(Long idfacturamodificaciones) {
        this.idfacturamodificaciones = idfacturamodificaciones;
    }
    public Long getIdfactura() {
        return idfactura;
    }
    public void setIdfactura(Long idfactura) {
        this.idfactura = idfactura;
    }
    public String getDatosfactura() {
        return datosfactura;
    }
    public void setDatosfactura(String datosfactura) {
        this.datosfactura = datosfactura;
    }
    public String getDetalle() {
        return detalle;
    }
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
    public LocalDate getFechacrea() {
        return fechacrea;
    }
    public void setFechacrea(LocalDate fechacrea) {
        this.fechacrea = fechacrea;
    } 
    
}
