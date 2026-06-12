package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;

import javax.persistence.*;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="rubroxfac")
@Where(clause = "(estado <> 0 or estado is null)")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rubroxfac {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idrubroxfac;
	private Float cantidad;
	private BigDecimal valorunitario;
	private Long estado; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idfactura_facturas")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Facturas idfactura_facturas;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idrubro_rubros")
	private Rubros idrubro_rubros;

}
