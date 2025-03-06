package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "valoresnc")
public class Valoresnc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvaloresnc; 
    private Long estado; 
    private BigDecimal valor; 
    private LocalDate fechaaplicado; 
    private BigDecimal saldo; 
    @ManyToOne
	@JoinColumn(name = "idntacredito_ntacredito")
    private Ntacredito idntacredito_ntacredito;

}
