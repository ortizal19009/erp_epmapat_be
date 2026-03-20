package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;

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
@Table(name = "pagoscobros")
public class Pagoscobros {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idpagcob;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "inttra")
   private Transaci inttra;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idbene")
   private Beneficiarios idbene;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idbenxtra")
   private BenexTran idbenxtra;
   private BigDecimal valor;
   private Long intpre; 
	private String codparreci; 
	private String codcuereci; 
	private Long asierefe;

}
