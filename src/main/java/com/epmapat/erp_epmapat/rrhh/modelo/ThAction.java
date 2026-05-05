package com.epmapat.erp_epmapat.rrhh.modelo;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "th_actions")
public class ThAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpersonal_personal", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler",
            "idcontemergencia_contemergencias",
            "idcargo_cargos",
            "idtpcontrato_tpcontratos"
    })
    private Personal idpersonal_personal;

    private String tipoaccion;
    private String motivo;
    private String observacion;
    private LocalDate fecvigencia;
    private LocalDate feccrea;
    private Long usucrea;
    private LocalDate fecmodi;
    private Long usumodi;
    private Boolean estado;
}
