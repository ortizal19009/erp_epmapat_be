package com.epmapat.erp_epmapat.modelo.administracion;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private String empresa; 
    private String ubirepo; 
    private String posiacti; 
    private String longacti; 
    private String naturaleza; 
    private LocalDate fechap; 
    private String nombre; 
    private String ubicomprobantes; 
    private String asunto; 
    private String textomail; 
    private String dirmatriz; 
    private LocalDate fechacierre;
    private String f_i;
    private String f_g; 
    private BigDecimal porciva; 
    private String ciudad; 
    private Long idtabla17; 
    private String ubidigi; 
    private String ubimagenes; 
    private String swpreingsin;

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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getUbirepo() {
        return ubirepo;
    }

    public void setUbirepo(String ubirepo) {
        this.ubirepo = ubirepo;
    }

    public String getPosiacti() {
        return posiacti;
    }

    public void setPosiacti(String posiacti) {
        this.posiacti = posiacti;
    }

    public String getLongacti() {
        return longacti;
    }

    public void setLongacti(String longacti) {
        this.longacti = longacti;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public LocalDate getFechap() {
        return fechap;
    }

    public void setFechap(LocalDate fechap) {
        this.fechap = fechap;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicomprobantes() {
        return ubicomprobantes;
    }

    public void setUbicomprobantes(String ubicomprobantes) {
        this.ubicomprobantes = ubicomprobantes;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getTextomail() {
        return textomail;
    }

    public void setTextomail(String textomail) {
        this.textomail = textomail;
    }

    public String getDirmatriz() {
        return dirmatriz;
    }

    public void setDirmatriz(String dirmatriz) {
        this.dirmatriz = dirmatriz;
    }

    public LocalDate getFechacierre() {
        return fechacierre;
    }

    public void setFechacierre(LocalDate fechacierre) {
        this.fechacierre = fechacierre;
    }

    public String getF_i() {
        return f_i;
    }

    public void setF_i(String f_i) {
        this.f_i = f_i;
    }

    public String getF_g() {
        return f_g;
    }

    public void setF_g(String f_g) {
        this.f_g = f_g;
    }

    public BigDecimal getPorciva() {
        return porciva;
    }

    public void setPorciva(BigDecimal porciva) {
        this.porciva = porciva;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Long getIdtabla17() {
        return idtabla17;
    }

    public void setIdtabla17(Long idtabla17) {
        this.idtabla17 = idtabla17;
    }

    public String getUbidigi() {
        return ubidigi;
    }

    public void setUbidigi(String ubidigi) {
        this.ubidigi = ubidigi;
    }

    public String getUbimagenes() {
        return ubimagenes;
    }

    public void setUbimagenes(String ubimagenes) {
        this.ubimagenes = ubimagenes;
    }

    public String getSwpreingsin() {
        return swpreingsin;
    }

    public void setSwpreingsin(String swpreingsin) {
        this.swpreingsin = swpreingsin;
    }
    

}
