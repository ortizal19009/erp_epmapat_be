package com.epmapat.erp_epmapat.modelo;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="personeriajuridica")
public class PersonJuridica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idpjuridica;
	private String descripcion;
	
}
