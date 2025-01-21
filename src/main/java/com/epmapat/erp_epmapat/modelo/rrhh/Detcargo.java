package com.epmapat.erp_epmapat.modelo.rrhh;

import java.math.BigDecimal;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "detcargo")
public class Detcargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddetcargo;
    private String rol;
    private String eje;
    private String grupoocupacional;
    private Boolean estado;
    private BigDecimal sueldo;

}
