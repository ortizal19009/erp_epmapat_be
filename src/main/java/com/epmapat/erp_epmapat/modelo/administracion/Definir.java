package com.epmapat.erp_epmapat.modelo.administracion;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] firma;
    private String clave_firma;
    private String email; 
    private String clave_email;
    private BigDecimal rbu;

}
