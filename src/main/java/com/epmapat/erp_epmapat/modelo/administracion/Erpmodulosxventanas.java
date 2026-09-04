package com.epmapat.erp_epmapat.modelo.administracion;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "erpmodulosxventanas")
public class Erpmodulosxventanas {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long iderpmoduloxventana;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "iderpmodulo", nullable = false)
   private Erpmodulos iderpmodulo;

   private String nombreventana;
}
