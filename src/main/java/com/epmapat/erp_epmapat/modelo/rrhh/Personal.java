package com.epmapat.erp_epmapat.modelo.rrhh;

import java.time.LocalDate;

import javax.persistence.*;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "personal")
public class Personal {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpersonal; 
    private String nombres; 
    private String apellidos; 
    private String identificacion; 
    private String email; 
    private String celular; 
    private String direccion; 
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcontemergencia_contemergencias")
    private Contemergencia idcontemergencia_contemergencias; 
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcargo_cargos")
    private Cargos idcargo_cargos; 
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtpcontrato_tpcontratos")
    private Tpcontratos idtpcontrato_tpcontratos; 
    private Long usucrea; 
    private LocalDate feccrea;
    private Long usumodi; 
    private LocalDate fecmodi;
    private Boolean estado; 
    private String codigo; 
    private LocalDate fecnacimiento; 
}
