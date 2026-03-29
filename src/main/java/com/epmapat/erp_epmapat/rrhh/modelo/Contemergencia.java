package com.epmapat.erp_epmapat.rrhh.modelo;

import javax.persistence.*;

import lombok.*;

@Entity(name = "RrhhLegacyContemergencia")
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contemergencia")
public class Contemergencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcontemergencia;
    private String nombre;
    private String celular;
    private String parentesco;

}

