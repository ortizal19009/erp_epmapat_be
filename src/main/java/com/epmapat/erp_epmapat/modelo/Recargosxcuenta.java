package com.epmapat.erp_epmapat.modelo;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recargosxcuenta")
public class Recargosxcuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrecargoxcuenta;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado_abonados")
    private Abonados idabonado_abonados;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision_emisiones")
    private Emisiones idemision_emisiones;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrubro_rubros")
    private Rubros idrubro_rubros;
    private int tipo;
    private String observacion;
    private Long usucrea;
    private Timestamp feccrea;
    private Long usumodi;
    private Timestamp fecmodi;
    private Long usuresp;
    private Timestamp fecha;
}
