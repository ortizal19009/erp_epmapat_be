package com.epmapat.erp_epmapat.modelo.rrhh;

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
@Table(name = "tpcontratos")
public class Tpcontratos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtpcontratos;
    private String descripcion;
}
