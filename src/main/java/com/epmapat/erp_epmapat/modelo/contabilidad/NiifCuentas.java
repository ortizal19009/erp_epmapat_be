package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "niifcuentas")
public class NiifCuentas {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idniifcue;
   private String codcue;
   private String nomcue;
   private String grucue;
   private Long nivcue;
   private Boolean movcue;

   private Long usucrea;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;

   private Long usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;

}
