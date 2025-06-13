package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ntacredito")
public class Ntacredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idntacredito;
    private String observacion;
    private BigDecimal valor;
    private BigDecimal devengado;
    private Long idtransfernota;
    private String razontransferencia;
    private String nrofactura;
    private Long usuarioeliminacion;
    private LocalDate fechaeliminacion;
    private String razoneliminacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente_clientes")
    private Clientes idcliente_clientes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado_abonados")
    private Abonados idabonado_abonados;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddocumento_documentos")
    private Documentos iddocumento_documentos;
    private String refdocumento;
}
