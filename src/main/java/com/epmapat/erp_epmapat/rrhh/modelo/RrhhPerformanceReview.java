package com.epmapat.erp_epmapat.rrhh.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rrhh_performance_reviews")
public class RrhhPerformanceReview extends RrhhAuditableEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private RrhhEmployee employee;

    @Column(nullable = false, length = 40)
    private String period;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal score;

    @Column(nullable = false, length = 120)
    private String reviewer;

    @Column(nullable = false)
    private LocalDate reviewDate;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(length = 500)
    private String comments;
}
