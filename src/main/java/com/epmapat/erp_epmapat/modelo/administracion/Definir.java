package com.epmapat.erp_epmapat.modelo.administracion;

import javax.persistence.*;

@Entity
@Table(name = "definir")

public class Definir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddefinir;
    private String razonsocial;
    private String nombrecomercial;
    private String ruc;
    private String direccion;
    private Byte tipoambiente;
    private float iva;

    public Long getIddefinir() {
        return iddefinir;
    }

    public void setIddefinir(Long iddefinir) {
        this.iddefinir = iddefinir;
    }

    public String getRazonsocial() {
        return razonsocial;
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public String getNombrecomercial() {
        return nombrecomercial;
    }

    public void setNombrecomercial(String nombrecomercial) {
        this.nombrecomercial = nombrecomercial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Byte getTipoambiente() {
        return tipoambiente;
    }

    public void setTipoambiente(Byte tipoambiente) {
        this.tipoambiente = tipoambiente;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

}
