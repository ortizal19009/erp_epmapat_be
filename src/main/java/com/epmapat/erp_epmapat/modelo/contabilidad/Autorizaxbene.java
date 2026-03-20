package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "autorizaxbene")
public class Autorizaxbene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idautoriza;
    private String numautoriza;
    private String numserie;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fechacaduca")
    private Date fechacaduca;
    private String facdesde;
    private String fachasta;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idbene")
    private Beneficiarios idbene;
    private Integer usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
    private Integer usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fecmodi")
    private Date fecmodi;

}
