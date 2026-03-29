package com.epmapat.erp_epmapat.rrhh.modelo;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rrhh_conflict_cases")
public class RrhhConflictCase extends RrhhAuditableEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private RrhhEmployee employee;

    @Column(nullable = false, length = 160)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false, length = 120)
    private String responsible;

    @Column(nullable = false)
    private LocalDate openedAt;

    @Column
    private LocalDate resolvedAt;

    @Lob
    private String resolution;
}
