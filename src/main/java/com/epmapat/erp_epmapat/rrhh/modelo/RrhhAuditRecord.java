package com.epmapat.erp_epmapat.rrhh.modelo;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rrhh_audits_v1")
public class RrhhAuditRecord extends RrhhAuditableEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 60)
    private String auditType;

    @Column(nullable = false, length = 160)
    private String title;

    @Lob
    private String findings;

    @Lob
    private String actionPlan;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false)
    private LocalDate auditDate;

    @Column(nullable = false, length = 120)
    private String responsible;
}
