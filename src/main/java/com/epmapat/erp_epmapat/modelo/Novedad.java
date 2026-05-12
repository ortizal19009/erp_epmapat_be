package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;

import javax.persistence.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "novedades")
public class Novedad implements Serializable{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnovedad;
    private String descripcion;
    Integer estado;
    Long usucrea;

    
    @Override
    public String toString() {
        return "Novedad [descripcion=" + descripcion + ", estado=" + estado + ", idnovedad=" + idnovedad + ", usucrea="
                + usucrea + "]";
    }
}
