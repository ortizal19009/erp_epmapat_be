package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "beneficiarios")
public class Beneficiarios {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idbene;
	private String codben;
	private String nomben;
	private String tpidben;
	private String rucben;
	private String ciben;
	private String tlfben;
	private String dirben;
	private String mailben;
	private Long tpcueben;
	private String cuebanben;
	private String foto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idgrupo")
	private Gruposbene idgrupo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idifinan")
	private Ifinan idifinan;
   
	private Long swconsufin;
	private Integer modulo;
	private Long usucrea;
	private LocalDate feccrea;
	private Long usumodi;
	private LocalDate fecmodi;

	
}
