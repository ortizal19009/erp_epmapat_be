package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "cliente_merge_clientes")
public class ClienteMergeCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Merge al que pertenece */
    @Column(name = "id_merge", nullable = false)
    private Long idMerge;

    /** Cliente duplicado absorbido */
    @Column(name = "cliente_dup_id", nullable = false)
    private Long clienteDupId;

    /** Fecha de registro (auditoría) */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public ClienteMergeCliente(Long idMerge, Long clienteDupId) {
        this.idMerge = idMerge;
        this.clienteDupId = clienteDupId;
    }

}