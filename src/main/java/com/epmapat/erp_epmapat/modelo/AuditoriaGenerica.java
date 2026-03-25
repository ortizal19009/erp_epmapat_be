package com.epmapat.erp_epmapat.modelo;

import java.sql.Timestamp;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auditoria_generica")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class AuditoriaGenerica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idauditoria;

    private String entidad; // e.g. recargosxcuenta, abonados, clientes, facturas

    private Long entidadId;

    private Long usumodi;

    private Timestamp fecmodi;

    private String tipo; // "MODIFICACION" | "ELIMINACION"

    private String observacion;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String objectJson;
}
