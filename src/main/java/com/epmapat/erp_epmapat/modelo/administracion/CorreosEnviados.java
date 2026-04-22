package com.epmapat.erp_epmapat.modelo.administracion;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "correos_enviados")
public class CorreosEnviados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcorreosenviado;

    private String modulo;

    private String documento;

    private Long documentoid;

    @Column(columnDefinition = "text")
    private String destinatarios;

    private String asunto;

    private String remitente;

    private String archivoadjunto;

    private String estado;

    @Column(columnDefinition = "text")
    private String detalle;

    private Timestamp fechaenvio;
}
