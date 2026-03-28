package com.epmapat.erp_epmapat.gestiondocumental.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tipos_documento")
public class TipoDocumento {

    @Id
    private UUID id;

    @Column(name = "entidad_id")
    private UUID entidadId;

    private String codigo;
    private String nombre;
    private Boolean activo;
    private String flujo;
}
