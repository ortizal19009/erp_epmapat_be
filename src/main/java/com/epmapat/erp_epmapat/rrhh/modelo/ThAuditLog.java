package com.epmapat.erp_epmapat.rrhh.modelo;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
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
@Table(name = "th_audit_log")
public class ThAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaudit;

    private String entidad;
    private Long idregistro;
    private String accion;
    private String detalle;
    private Long usuario;
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
    }
}
