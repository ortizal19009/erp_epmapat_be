package com.epmapat.erp_epmapat.modelo;

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
@Table(name = "facxnc")
public class Facxnc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacxnc;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura_facturas")
    private Facturas idfactura_facturas;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvaloresnc_valoresnc")
    private Valoresnc idvaloresnc_valoresnc;
}
