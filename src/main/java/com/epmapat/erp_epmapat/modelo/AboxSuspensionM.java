package com.epmapat.erp_epmapat.modelo;

import javax.persistence.*;

@Entity
@Table(name = "aboxsuspension")

public class AboxSuspensionM {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idaboxsuspen;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idsuspension_suspensiones")
	private SuspensionesM idsuspension_suspensiones;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idabonado_abonados")
	private Abonados idabonado_abonados;

	public AboxSuspensionM() {
		super();
	}

	public AboxSuspensionM(Long idaboxsuspen, SuspensionesM idsuspension_suspensiones, Abonados idabonado_abonados) {
		super();
		this.idaboxsuspen = idaboxsuspen;
		this.idsuspension_suspensiones = idsuspension_suspensiones;
		this.idabonado_abonados = idabonado_abonados;
	}

	public Long getIdaboxsuspen() {
		return idaboxsuspen;
	}
	public void setIdaboxsuspen(Long idaboxsuspen) {
		this.idaboxsuspen = idaboxsuspen;
	}

	public SuspensionesM getIdsuspension_suspensiones() {
		return idsuspension_suspensiones;
	}
	public void setIdsuspension_suspensiones(SuspensionesM idsuspension_suspensiones) {
		this.idsuspension_suspensiones = idsuspension_suspensiones;
	}

	public Abonados getIdabonado_abonados() {
		return idabonado_abonados;
	}
	public void setIdabonado_abonados(Abonados idabonado_abonados) {
		this.idabonado_abonados = idabonado_abonados;
	}
	
}
