package com.epmapat.erp_epmapat.rrhh.modelo;

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
@Table(name = "th_leave_requests")
public class ThLeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrequest;

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

    private String tipolicencia;
    private LocalDate fechainicio;
    private LocalDate fechafin;
    private BigDecimal dias_solicitados;
    private String motivo;
    private String estado;
    private Long aprobador_id;
    private LocalDate fecha_aprobacion;
    private String observacion_aprobacion;
    private LocalDate feccrea;
    private Long usucrea;
    private LocalDate fecmodi;
    private Long usumodi;
    private Boolean activo;
}
