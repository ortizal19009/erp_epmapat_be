package com.epmapat.erp_epmapat.rrhh.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Evaluaciones del catálogo Personal usado por la ficha individual. */
@Entity
@Table(name = "th_performance_reviews")
@Getter @Setter
public class ThPerformanceReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idreview;
    @Column(nullable = false) private Long idpersonal;
    @Column(nullable = false, length = 40) private String periodo;
    @Column(nullable = false) private LocalDate fecha;
    @Column(nullable = false, precision = 5, scale = 2) private BigDecimal puntaje;
    @Column(nullable = false, length = 120) private String evaluador;
    @Column(nullable = false, length = 2000) private String observaciones;
    @Column(name = "plan_mejora", length = 2000) private String planMejora;
    @Column(nullable = false) private Long usuario;
    @Column(nullable = false) private LocalDateTime creado;
}
