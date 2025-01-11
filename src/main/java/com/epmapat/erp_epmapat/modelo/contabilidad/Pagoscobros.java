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

@Entity
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

   public Long getIdpagcob() {
      return idpagcob;
   }
   public void setIdpagcob(Long idpagcob) {
      this.idpagcob = idpagcob;
   }
   public Transaci getInttra() {
      return inttra;
   }
   public void setInttra(Transaci inttra) {
      this.inttra = inttra;
   }
   public Beneficiarios getIdbene() {
      return idbene;
   }
   public void setIdbene(Beneficiarios idbene) {
      this.idbene = idbene;
   }
   public BenexTran getIdbenxtra() {
      return idbenxtra;
   }
   public void setIdbenxtra(BenexTran idbenxtra) {
      this.idbenxtra = idbenxtra;
   }
   public BigDecimal getValor() {
      return valor;
   }
   public void setValor(BigDecimal valor) {
      this.valor = valor;
   }
   public Long getIntpre() {
      return intpre;
   }
   public void setIntpre(Long intpre) {
      this.intpre = intpre;
   }
   public String getCodparreci() {
      return codparreci;
   }
   public void setCodparreci(String codparreci) {
      this.codparreci = codparreci;
   }
   public String getCodcuereci() {
      return codcuereci;
   }
   public void setCodcuereci(String codcuereci) {
      this.codcuereci = codcuereci;
   }
   public Long getAsierefe() {
      return asierefe;
   }
   public void setAsierefe(Long asierefe) {
      this.asierefe = asierefe;
   }

}
