package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "facturas")
public class Facturas implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idfactura;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idmodulo")
	private Modulos idmodulo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente")
	private Clientes idcliente;
	private String nrofactura;
	private Long porcexoneracion;
	private String razonexonera;
	private BigDecimal totaltarifa;
	private Integer pagado;
	private Long usuariocobro;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE_TIME)
	// @Column(name = "fechacobro")
	private LocalDate fechacobro;
	private Long estado;
	private Long usuarioanulacion;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "fechaanulacion")
	private LocalDate fechaanulacion;
	private String razonanulacion;
	private Long usuarioeliminacion;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso= ISO.DATE)
	// @Column ( name = "fechaeliminacion")
	private LocalDate fechaeliminacion;
	private String razoneliminacion;
	private Long conveniopago;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso= ISO.DATE)
	// @Column(name = "fechaconvenio")
	private LocalDate fechaconvenio;
	private Long estadoconvenio;
	private Long formapago;
	private String refeformapago;
	private String horacobro;
	private Long usuariotransferencia;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "fechatransferencia")
	private LocalDate fechatransferencia;
	private Long usucrea;

	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso=ISO.DATE)
	// @Column(name = "feccrea")
	private LocalDate feccrea;
	private Long usumodi;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "fecmodi")
	private LocalDate fecmodi;
	private BigDecimal valorbase;
	private Long idabonado;
	private BigDecimal interescobrado;
	private BigDecimal swiva;
	private Boolean swcondonar; 
	private BigDecimal valornotacredito;
	private String secuencialfacilito;

}
