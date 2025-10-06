package com.epmapat.erp_epmapat.modelo;

import javax.persistence.*;

@Entity
@Table(name ="personeriajuridica")

public class PersonJuridica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idpjuridica;
	private String descripcion;
	
	public PersonJuridica() {
		super();
	}

	public PersonJuridica(Long idpjuridica, String descripcion) {
		super();
		this.idpjuridica = idpjuridica;
		this.descripcion = descripcion;
	}

	public Long getIdpjuridica() {
		return idpjuridica;
	}

	public void setIdpjuridica(Long idpjuridica) {
		this.idpjuridica = idpjuridica;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
