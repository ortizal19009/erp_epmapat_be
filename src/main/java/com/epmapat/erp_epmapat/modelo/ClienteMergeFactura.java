package com.epmapat.erp_epmapat.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "cliente_merge_facturas")
public class ClienteMergeFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Merge al que pertenece */
    @Column(name = "id_merge", nullable = false)
    private Long idMerge;

    /** Factura que fue reasignada */
    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    /** Cliente original de la factura */
    @Column(name = "cliente_origen", nullable = false)
    private Long clienteOrigen;

    /** Fecha de registro (auditoría) */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public ClienteMergeFactura(Long idMerge, Long facturaId, Long clienteOrigen) {
        this.idMerge = idMerge;
        this.facturaId = facturaId;
        this.clienteOrigen = clienteOrigen;
    }

}