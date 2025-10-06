package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tmpinteresxfac")
public class Tmpinteresxfac {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtmpinteresxfac;
    private Long idfactura;
    private BigDecimal interesapagar;
    private LocalDateTime  feccorte;
}
