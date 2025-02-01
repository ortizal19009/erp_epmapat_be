package com.epmapat.erp_epmapat.modelo.coactivas;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.Facturas;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "facxremi")
public class Facxremi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacxremi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura_facturas")
    private Facturas idfactura_facturas;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idremision_remisiones")
    private Remision idremision_remisiones;
    private Long cuota;
    private Long tipfactura;
}
