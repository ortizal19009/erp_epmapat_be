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
@Table(name = "niveles")
public class Niveles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idnivel; 
	private String nomniv; 
	private Integer nivcue;


}
