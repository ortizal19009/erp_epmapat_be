package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

import lombok.*;

@Getter
@Setter

@Entity
@Table(name = "asientos")
public class Asientos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idasiento;
	private Long asiento;
	private LocalDate fecha;
	private Integer tipasi;
	private Integer tipcom;
	private Long compro;
	private Long numcue;
	private BigDecimal totdeb;
	private BigDecimal totcre;
	private String glosa;
	private String numdoc;
	private String numdocban;
	private Integer cerrado;
	private Integer swretencion;
	private Long totalspi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intdoc")
	private Documentos intdoc;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene;
	private Long idcueban;
	private Long usucrea;
	private Date feccrea;
	private Long usumodi;
	private Date fecmodi;

}
