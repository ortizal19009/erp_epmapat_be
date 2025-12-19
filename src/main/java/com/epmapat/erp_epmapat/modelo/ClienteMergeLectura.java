package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private Long idMergeLectura;

    private Long idMerge;
    private Long idLectura;
    private Long idClienteOrigen;

    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
