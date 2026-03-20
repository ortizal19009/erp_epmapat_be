package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor	
@AllArgsConstructor
@Table(name = "benextran")
public class BenexTran {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idbenxtra; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inttra")
	private Transaci inttra; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intdoc")
	private Documentos intdoc; 
	private String numdoc; 
	private BigDecimal valor; 
	private BigDecimal totpagcob; 
	private BigDecimal pagocobro; 
	private Long idpagcob; 
	private Long intpre; 
	private String codparreci; 
	private String codcuereci; 
	private Long asierefe;
	

}
