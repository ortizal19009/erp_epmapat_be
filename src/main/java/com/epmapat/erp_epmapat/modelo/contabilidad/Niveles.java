package com.epmapat.erp_epmapat.modelo.contabilidad;

import javax.persistence.*;

@Entity
@Table(name = "niveles")
public class Niveles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idnivel; 
	private String nomniv; 
	private Integer nivcue;

	public Niveles(Long idnivel, String nomniv, Integer nivcue) {
		super();
		this.idnivel = idnivel;
		this.nomniv = nomniv;
		this.nivcue = nivcue;
	}

	public Niveles() {
		super();
		// TODO Auto-generated constructor stub
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
	public Integer getNivcue() {
		return nivcue;
	}
	public void setNivcue(Integer nivcue) {
		this.nivcue = nivcue;
	} 

}
