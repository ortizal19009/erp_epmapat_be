package com.epmapat.erp_epmapat.modelo.administracion;

import java.time.ZonedDateTime;

import javax.persistence.*;

@Entity
@Table(name = "documentos")

public class Documentos {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long intdoc;
	private String nomdoc;
	private Long tipdoc;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtabla4")
	private Tabla4 idtabla4;
	private String tipocomprobante; 
	private Long usucrea;

	@Column(name = "feccrea")
	private ZonedDateTime feccrea;

	private Long usumodi;

	@Column(name = "fecmodi")
	private ZonedDateTime fecmodi;

	public Long getIntdoc() {
		return intdoc;
	}

	public void setIntdoc(Long intdoc) {
		this.intdoc = intdoc;
	}

	public String getNomdoc() {
		return nomdoc;
	}

	public void setNomdoc(String nomdoc) {
		this.nomdoc = nomdoc;
	}

	public Long getTipdoc() {
		return tipdoc;
	}

	public void setTipdoc(Long tipdoc) {
		this.tipdoc = tipdoc;
	}

	public Tabla4 getIdtabla4() {
		return idtabla4;
	}

	public void setIdtabla4(Tabla4 idtabla4) {
		this.idtabla4 = idtabla4;
	}

	public Long getUsucrea() {
		return usucrea;
	}

	public void setUsucrea(Long usucrea) {
		this.usucrea = usucrea;
	}

	public ZonedDateTime getFeccrea() {
		return feccrea;
	}

	public void setFeccrea(ZonedDateTime feccrea) {
		this.feccrea = feccrea;
	}

	public Long getUsumodi() {
		return usumodi;
	}

	public void setUsumodi(Long usumodi) {
		this.usumodi = usumodi;
	}

	public ZonedDateTime getFecmodi() {
		return fecmodi;
	}

	public void setFecmodi(ZonedDateTime fecmodi) {
		this.fecmodi = fecmodi;
	}

	public String getTipocomprobante() {
		return tipocomprobante;
	}

	public void setTipocomprobante(String tipocomprobante) {
		this.tipocomprobante = tipocomprobante;
	}
	

}
