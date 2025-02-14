package com.epmapat.erp_epmapat.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "impuestos")
public class Impuestos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idimpuesto;
    private Long codigo;
    private String descripcion;
    private Long valor;
    private Boolean estado;
}
