package com.epmapat.erp_epmapat.modelo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cliente_merge_abonados")
public class ClienteMergeAbonado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Merge al que pertenece */
    @Column(name = "id_merge", nullable = false)
    private Long idMerge;

    /** Abonado que fue reasignado */
    @Column(name = "abonado_id", nullable = false)
    private Long abonadoId;

    /** Cliente original del abonado */
    @Column(name = "cliente_origen", nullable = false)
    private Long clienteOrigen;

    /** Fecha del registro (auditoría) */
    @Column(name = "fecha_merge", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public ClienteMergeAbonado(Long idMerge, Long abonadoId, Long clienteOrigen) {
        this.idMerge = idMerge;
        this.abonadoId = abonadoId;
        this.clienteOrigen = clienteOrigen;
    }

}
