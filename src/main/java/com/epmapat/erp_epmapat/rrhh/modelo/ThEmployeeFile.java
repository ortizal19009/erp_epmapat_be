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
@Table(name = "th_employee_files")
public class ThEmployeeFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfile;

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

    private String tipo_doc;
    private String nombre_archivo;
    private String ruta_archivo;
    private String hash_archivo;
    private Integer version_doc;
    private String estado;
    private LocalDate feccrea;
    private Long usucrea;
    private LocalDate fecmodi;
    private Long usumodi;
    @javax.persistence.Column(length = 2000)
    private String descripcion;
    @javax.persistence.Column(length = 120)
    private String numero_documento;
    @javax.persistence.Column(length = 150)
    private String emisor;
    private LocalDate fecha_emision;
    private LocalDate fecha_vencimiento;
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private String origen;
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private String nombre_original;
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private String content_type;
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private Long tamano_bytes;
}
