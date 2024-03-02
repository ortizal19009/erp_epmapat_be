package com.epmapat.erp_epmapat.modelo.contabilidad;

import javax.persistence.*;

@Entity
@Table(name = "niveles")
public class Niveles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idnivel; 
	private String nomniv; 
	private Long nivcue;
	public Niveles(Long idnivel, String nomniv, Long nivcue) {
		super();
		this.idnivel = idnivel;
		this.nomniv = nomniv;
		this.nivcue = nivcue;
	}
	public Niveles() {
		super();
	
	}
	public Long getIdnivel() {
		return idnivel;
	}
	public void setIdnivel(Long idnivel) {
		this.idnivel = idnivel;
	}
	public String getNomniv() {
		return nomniv;
	}
	public void setNomniv(String nomniv) {
		this.nomniv = nomniv;
	}
	public Long getNivcue() {
		return nivcue;
	}
	public void setNivcue(Long nivcue) {
		this.nivcue = nivcue;
	} 
	

}
