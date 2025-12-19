package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDateTime;

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
@Table(name = "cliente_merge")
public class ClienteMerge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_merge")
    private Long idMerge;

    @Column(name = "master_id", nullable = false)
    private Long masterId;

    @Column(name = "fecha_merge", nullable = false)
    private LocalDateTime fechaMerge = LocalDateTime.now();

    @Column(name = "usuario_merge")
    private String usuarioMerge;

    @Column(name = "observacion")
    private String observacion;
}
