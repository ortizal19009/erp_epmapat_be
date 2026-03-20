package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ejecucio")
public class Ejecucio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long inteje;
    private String codpar;
    @Column(name = "fecha_eje")
    private LocalDate fecha_eje;
    private Integer tipeje;
    private BigDecimal modifi;
    private BigDecimal prmiso;
    private BigDecimal totdeven;
    private BigDecimal devengado;
    private BigDecimal cobpagado;
    private String concep;
    private Integer usucrea;
    @Column(name = "feccrea")
    private LocalDate feccrea;
    private Integer usumodi;
    @Column(name = "fecmodi")
    private LocalDate fecmodi;
    private Long idrefo;
    private Long idtrami;
    private Long idparxcer;
    private Long idasiento;
    private Long inttra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intpre")
    private Presupue intpre;

    private Long idprmiso;
    private Long idevenga;
        
}
