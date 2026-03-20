package com.epmapat.erp_epmapat.modelo.contabilidad;

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
@Table(name = "ifinan")
public class Ifinan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idifinan;
	private String codifinan;
	private String nomifinan;
	private String foto;

}
