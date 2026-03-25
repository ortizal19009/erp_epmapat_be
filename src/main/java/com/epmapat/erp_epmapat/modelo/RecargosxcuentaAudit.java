package com.epmapat.erp_epmapat.modelo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "recargosxcuenta_audit")
public class RecargosxcuentaAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrecargoxcuenta_audit;

    @ManyToOne
    @JoinColumn(name = "idrecargoxcuenta_recargosxcuenta")
    private Recargosxcuenta recargosxcuenta;

    private Long usumodi;

    private Timestamp fecmodi;

    private String observacion;

    private String tipo; // MODIFICACION, ELIMINACION

    @Column(columnDefinition = "jsonb")
    private String object_json;
}
