package com.epmapat.erp_epmapat.modelo.contabilidad;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "niifhomologa")
public class NiifHomologa {
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private Long idhomologa;
	private String codcueniif;
	private String codcue;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idniifcue")
	private NiifCuentas idniifcue;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcuenta")
	private Cuentas idcuenta;

}
