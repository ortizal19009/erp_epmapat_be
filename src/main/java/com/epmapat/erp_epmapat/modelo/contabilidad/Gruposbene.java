package com.epmapat.erp_epmapat.modelo.contabilidad;

import javax.persistence.*;

@Entity
@Table(name = "gruposbene")

public class Gruposbene {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idgrupo;
	private String codgru;
	private String nomgru;
	private Long modulo1;
	private Long modulo2;
	private Long modulo3;
	private Long modulo4;
	private Long modulo5;
	private Long modulo6;

	public Gruposbene() {
		super();
	}

	public Gruposbene(Long idgrupo, String codgru, String nomgru, Long modulo1, Long modulo2, Long modulo3,
			Long modulo4, Long modulo5, Long modulo6) {
		super();
		this.idgrupo = idgrupo;
		this.codgru = codgru;
		this.nomgru = nomgru;
		this.modulo1 = modulo1;
		this.modulo2 = modulo2;
		this.modulo3 = modulo3;
		this.modulo4 = modulo4;
		this.modulo5 = modulo5;
		this.modulo6 = modulo6;
	}
   
	public Long getIdgrupo() {
		return idgrupo;
	}
	public void setIdgrupo(Long idgrupo) {
		this.idgrupo = idgrupo;
	}
	public String getCodgru() {
		return codgru;
	}
	public void setCodgru(String codgru) {
		this.codgru = codgru;
	}
	public String getNomgru() {
		return nomgru;
	}
	public void setNomgru(String nomgru) {
		this.nomgru = nomgru;
	}
	public Long getModulo1() {
		return modulo1;
	}
	public void setModulo1(Long modulo1) {
		this.modulo1 = modulo1;
	}
	public Long getModulo2() {
		return modulo2;
	}
	public void setModulo2(Long modulo2) {
		this.modulo2 = modulo2;
	}
	public Long getModulo3() {
		return modulo3;
	}
	public void setModulo3(Long modulo3) {
		this.modulo3 = modulo3;
	}
	public Long getModulo4() {
		return modulo4;
	}
	public void setModulo4(Long modulo4) {
		this.modulo4 = modulo4;
	}
	public Long getModulo5() {
		return modulo5;
	}
	public void setModulo5(Long modulo5) {
		this.modulo5 = modulo5;
	}
	public Long getModulo6() {
		return modulo6;
	}
	public void setModulo6(Long modulo6) {
		this.modulo6 = modulo6;
	}
}
