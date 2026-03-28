package com.epmapat.erp_epmapat.gestiondocumental.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "dependencias")
public class Dependencia {

    @Id
    private UUID id;

    @Column(name = "entidad_id")
    private UUID entidadId;

    private String codigo;
    private String nombre;

    @Column(name = "padre_id")
    private UUID padreId;

    private Boolean activo;

    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}
