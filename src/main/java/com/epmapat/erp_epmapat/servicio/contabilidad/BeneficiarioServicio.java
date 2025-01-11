package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.modelo.contabilidad.Gruposbene;
import com.epmapat.erp_epmapat.repositorio.contabilidad.BeneficiariosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.GruposbeneR;

@Service
public class BeneficiarioServicio {

   @Autowired
   private BeneficiariosR dao;
   @Autowired
   private GruposbeneR dao1;

   // Lista de beneficiarios por nombre, codigo y (ruc o ci)
   public List<Beneficiarios> findBeneficiarios(String nomben, String codben, String rucben, String ciben) {
      return dao.findBeneficiarios(nomben, codben, rucben, ciben);
   }

   public List<Beneficiarios> findByNomben(String nomben) {
      return dao.findByNomben(nomben);
   }

   //Busca por nombre y grupo
   public List<Beneficiarios> findByNombenGru(String nomben, Long idgrupo) {
      return dao.findByNombenGru(nomben, idgrupo);
   }

   // Último código de Beneficiario (por grupo)
   public Beneficiarios findUltCodigo(Long idgrupo) {
      return dao.findUltCodigo(idgrupo);
   }

   // Valida el Nombre del Beneficiario
   public boolean valNomben(String nomben) {
      return dao.valNomben(nomben);
   }

   // Siguiente código de Beneficiario (por grupo)
   public String siguienteCodigo(Long idgrupo) {
      Beneficiarios ultimoBene = dao.findUltCodigo(idgrupo);
      if (ultimoBene != null) {
         String ultimoCodigo = ultimoBene.getCodben();
         String prefijo = ultimoCodigo.substring(0, 2); // Obtiene "P-"
         String parteNumerica = ultimoCodigo.substring(2); // Obtiene "0151"
         int numeroEntero = Integer.parseInt(parteNumerica); // Convierte "0151" a 151
         int siguienteNumero = numeroEntero + 1; // Obtiene 152
         String siguienteParteNumerica = String.format("%04d", siguienteNumero); // Obtiene "0152"
         String siguienteCodigo = prefijo + siguienteParteNumerica; // Obtiene "P-0152"
         return siguienteCodigo;
      } else {
         Gruposbene grupo = dao1.findByIdgrupo(idgrupo);
         if (grupo != null) {
            String codgru = grupo.getCodgru();
            return codgru + "-0001";
         } else {
            return "-0001";
         }
      }
   }

   // Validar Código de Beneficiario
   public boolean valCodben(String codben) {
      return dao.valCodben(codben);
   }

   // Valida el RUC del Beneficiario
   public boolean valRucben(String rucben) {
      return dao.valRucben(rucben);
   }

   // Valida la CI del Beneficiario
   public boolean valCiben(String ciben) {
      return dao.valCiben(ciben);
   }

   // Cuenta por Idifinan (Instituciones financieras)
   public Long countByIdifinan(Long idifinan) {
      return dao.countByIdifinan(idifinan);
   }

   public <S extends Beneficiarios> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Beneficiarios> findById(Long id) {
      return dao.findById(id);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}
