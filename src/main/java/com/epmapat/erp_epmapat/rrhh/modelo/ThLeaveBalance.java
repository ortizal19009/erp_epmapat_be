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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "th_leave_balances")
public class ThLeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idbalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpersonal_personal", nullable = false)
    private Personal idpersonal_personal;

    private Integer anio;
    private BigDecimal dias_asignados;
    private BigDecimal dias_usados;
    private BigDecimal dias_disponibles;
    private LocalDate feccrea;
    private Long usucrea;
    private LocalDate fecmodi;
    private Long usumodi;
    private Boolean estado;
}
