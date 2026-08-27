package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "facturas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Facturas implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idfactura;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idmodulo")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Modulos idmodulo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Clientes idcliente;
	private String nrofactura;
	private Long porcexoneracion;
	private String razonexonera;
	private BigDecimal totaltarifa;
	private Integer pagado;
	private Long usuariocobro;
	private LocalDate fechacobro;
	private Long estado;
	private Long usuarioanulacion;
	private LocalDate fechaanulacion;
	private String razonanulacion;
	private Long usuarioeliminacion;
	private LocalDate fechaeliminacion;
	private String razoneliminacion;
	private Long conveniopago;
	private LocalDate fechaconvenio;
	private Long estadoconvenio;
	private Long formapago;
	private String refeformapago;
	@JsonFormat(pattern = "H:m:s")
	private LocalTime horacobro;
	private Long usuariotransferencia;
	private LocalDate fechatransferencia;
	private Long usucrea;
	private LocalDate feccrea;
	private Long usumodi;
	private LocalDate fecmodi;
	private BigDecimal valorbase;
	private Long idabonado;
	private BigDecimal interescobrado;
	private BigDecimal swiva;
	private Boolean swcondonar; 
	private BigDecimal valornotacredito;
	private String secuencialfacilito;
	private Timestamp fechacompensacion;
	private Boolean swinteres;
	private Boolean swmulta;

}
