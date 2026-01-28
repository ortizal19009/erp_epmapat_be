package com.epmapat.erp_epmapat.modelo.rrhh;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cargos")
public class Cargos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcargo;
    private String descripcion;
    private Boolean estado;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddetcargo_detcargo")
    private Detcargo iddetcargo_detcargo;
}
