package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Presupue intpre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtrami", referencedColumnName = "idtrami", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tramipresu tramipresu;

    private Long idprmiso;
    private Long idevenga;
        
}
