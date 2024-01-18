package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "rubroadicional")

public class RubroAdicionalM {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idrubroadicional;
	private Float valor;
	private Long swiva;
	private Long rubroprincipal;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Column(name = "fecmodi")
	private Date fecmodi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idrubro_rubros")
	private Rubros idrubro_rubros;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtptramite_tptramite")
	private TpTramiteM idtptramite_tptramite;
	
	public RubroAdicionalM() {
		super();
	}
	
	public RubroAdicionalM(Long idrubroadicional, Float valor, Long swiva, Long rubroprincipal, Long usucrea,
			Date feccrea, Long usumodi, Date fecmodi, Rubros idrubro_rubros, TpTramiteM idtptramite_tptramite) {
		super();
		this.idrubroadicional = idrubroadicional;
		this.valor = valor;
		this.swiva = swiva;
		this.rubroprincipal = rubroprincipal;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.idrubro_rubros = idrubro_rubros;
		this.idtptramite_tptramite = idtptramite_tptramite;
	}

	public Long getIdrubroadicional() {
		return idrubroadicional;
	}
	public void setIdrubroadicional(Long idrubroadicional) {
		this.idrubroadicional = idrubroadicional;
	}
	public Float getValor() {
		return valor;
	}
	public void setValor(Float valor) {
		this.valor = valor;
	}
	public Long getSwiva() {
		return swiva;
	}
	public void setSwiva(Long swiva) {
		this.swiva = swiva;
	}
	public Long getRubroprincipal() {
		return rubroprincipal;
	}
	public void setRubroprincipal(Long rubroprincipal) {
		this.rubroprincipal = rubroprincipal;
	}
	public Long getUsucrea() {
		return usucrea;
	}
	public void setUsucrea(Long usucrea) {
		this.usucrea = usucrea;
	}
	public Date getFeccrea() {
		return feccrea;
	}
	public void setFeccrea(Date feccrea) {
		this.feccrea = feccrea;
	}
	public Long getUsumodi() {
		return usumodi;
	}
	public void setUsumodi(Long usumodi) {
		this.usumodi = usumodi;
	}
	public Date getFecmodi() {
		return fecmodi;
	}
	public void setFecmodi(Date fecmodi) {
		this.fecmodi = fecmodi;
	}
	public Rubros getIdrubro_rubros() {
		return idrubro_rubros;
	}
	public void setIdrubro_rubros(Rubros idrubro_rubros) {
		this.idrubro_rubros = idrubro_rubros;
	}
	public TpTramiteM getIdtptramite_tptramite() {
		return idtptramite_tptramite;
	}
	public void setIdtptramite_tptramite(TpTramiteM idtptramite_tptramite) {
		this.idtptramite_tptramite = idtptramite_tptramite;
	}
	
}
