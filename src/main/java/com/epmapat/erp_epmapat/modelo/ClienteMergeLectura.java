package com.epmapat.erp_epmapat.modelo;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente_merge_lecturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteMergeLectura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_merge_lectura")
    private Long idMergeLectura;

    @Column(name = "id_merge", nullable = false)
    private Long idMerge;

    @Column(name = "id_lectura", nullable = false)
    private Long idLectura;

    @Column(name = "id_cliente_origen", nullable = false)
    private Long idClienteOrigen;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = Timestamp.valueOf(LocalDateTime.now());
        }
    }
}
