package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name= "cuotas")

public class Cuotas {
   
   @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcuota;
   private Long nrocuota;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idfactura")
   private Facturas idfactura;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idconvenio_convenios")
	private Convenios idconvenio_convenios;
   private Long usucrea;
	
   private LocalDate feccrea;
   
   public Long getIdcuota() {
      return idcuota;
   }
   public void setIdcuota(Long idcuota) {
      this.idcuota = idcuota;
   }
   public Long getNrocuota() {
      return nrocuota;
   }
   public void setNrocuota(Long nrocuota) {
      this.nrocuota = nrocuota;
   }
   public Facturas getIdfactura() {
      return idfactura;
   }
   public void setIdfactura(Facturas idfactura) {
      this.idfactura = idfactura;
   }
   public Convenios getIdconvenio_convenios() {
      return idconvenio_convenios;
   }
   public void setIdconvenio_convenios(Convenios idconvenio_convenios) {
      this.idconvenio_convenios = idconvenio_convenios;
   }
   public Long getUsucrea() {
      return usucrea;
   }
   public void setUsucrea(Long usucrea) {
      this.usucrea = usucrea;
   }
   public LocalDate getFeccrea() {
      return feccrea;
   }
   public void setFeccrea(LocalDate feccrea) {
      this.feccrea = feccrea;
   }

   

}
