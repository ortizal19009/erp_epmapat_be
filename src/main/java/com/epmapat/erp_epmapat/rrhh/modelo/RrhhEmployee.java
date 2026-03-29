package com.epmapat.erp_epmapat.rrhh.modelo;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rrhh_employees")
public class RrhhEmployee extends RrhhAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 20)
    private String identification;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(nullable = false, length = 100)
    private String area;

    @Column(nullable = false, length = 100)
    private String dependency;

    @Column(name = "job_title", nullable = false, length = 120)
    private String jobTitle;

    @Column(name = "contract_type", nullable = false, length = 60)
    private String contractType;

    @Column(name = "employment_status", nullable = false, length = 40)
    private String employmentStatus;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "professional_title", length = 120)
    private String professionalTitle;

    @Column(nullable = false)
    private Boolean active;
}
