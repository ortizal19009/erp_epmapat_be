package com.epmapat.erp_epmapat.modelo.administracion;

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
@Table(name = "erpmodulos")
public class Erpmodulos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iderpmodulo;
    private String descripcion;
    private String platform;
}
