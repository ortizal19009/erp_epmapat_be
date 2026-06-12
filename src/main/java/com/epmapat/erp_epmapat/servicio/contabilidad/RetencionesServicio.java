package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Asientos;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.administracion.DocumentosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.AsientosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.BeneficiariosR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla01R;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla15R;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla17R;

@Service
public class RetencionesServicio {

   @Autowired
   private RetencionesR dao;
   @Autowired
   private AsientosR daoAsientos;
   @Autowired
   private BeneficiariosR daoBeneficiarios;
   @Autowired
   private DocumentosR daoDocumentos;
   @Autowired
   private Tabla01R daoTabla01;
   @Autowired
   private Tabla15R daoTabla15;
   @Autowired
   private Tabla17R daoTabla17;

   // Busca por secuencial y fechas
   @Transactional
   public List<Retenciones> findDesdeHasta(String desdeSecu, String hastaSecu, Date desdeFecha, Date hastaFecha) {
      List<Retenciones> retenciones = dao.findDesdeHasta(desdeSecu, hastaSecu, desdeFecha, hastaFecha);
      if (retenciones.isEmpty()) {
         throw new ResourceNotFoundExcepciones("No se encontraron retenciones con los criterios proporcionados.");
      }

      // Mantener la sesión abierta solo durante la creación de la respuesta.
      for (Retenciones r : retenciones) {
         if (r.getIdbene() != null) {
            r.getIdbene().getNomben();
         }
      }

      return retenciones;
   }

   public Retenciones findLastNumeric() {
      return dao.findLastNumeric();
   }

   // Valida Secretencion1
   public boolean valSecretencion1(Integer secretencion1) {
      return dao.valSecretencion1(secretencion1);
   }

   public List<Retenciones> findAll() {
      return dao.findAll();
   }

   public Optional<Retenciones> findById(Long id) {
      return dao.findById(id);
   }

   // Retencion(es) de un asiento
   public List<Retenciones> findByIdasiento(Long idasiento) {
      return dao.findByIdasiento(idasiento);
   }

   // Guarda: Las Tablas foraneas se crean aqui, el front solo envia el ID
   public Retenciones save(Retenciones r) {
      r.setIdasiento(daoAsientos.getReferenceById(r.getIdasiento().getIdasiento()));
      r.setIdbene(daoBeneficiarios.getReferenceById(r.getIdbene().getIdbene()));
      r.setIddocu(daoDocumentos.getReferenceById(r.getIddocu().getIntdoc()));
      r.setIdtabla01(daoTabla01.getReferenceById(r.getIdtabla01().getIdtabla01()));
      if (r.getIdtabla15() != null && r.getIdtabla15().getIdtabla15() != null) {
         r.setIdtabla15(daoTabla15.getReferenceById(r.getIdtabla15().getIdtabla15()));
      } else {
         r.setIdtabla15(null);
      }
      r.setIdtabla17(daoTabla17.getReferenceById(r.getIdtabla17().getIdtabla17()));
      // ACTUALIZA ASIENTO.swretencion
      Asientos asiento = daoAsientos.getReferenceById(r.getIdasiento().getIdasiento());
      asiento.setSwretencion(1);
      daoAsientos.save(asiento);
      // Guardar la retención
      return dao.save(r);
   }

   // Actualiza
   public Retenciones updateRetencion(Long idrete, Retenciones data) {
      Retenciones retenciones = findById(idrete)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  "No se encuentra este Id " + idrete));
      // Coloca campos
      retenciones.setFecharegistro(data.getFecharegistro());
      retenciones.setFechaemision(data.getFechaemision());
      retenciones.setNumdoc(data.getNumdoc());
      retenciones.setPorciva(data.getPorciva());
      // retenciones.setSwretencion(data.getSwretencion());
      retenciones.setBaseimponible(data.getBaseimponible());
      retenciones.setBaseimpgrav(data.getBaseimpgrav());
      retenciones.setBasenograiva(data.getBasenograiva());
      retenciones.setBaseimpice(data.getBaseimpice());
      retenciones.setMontoiva(data.getMontoiva());
      retenciones.setPorcentajeice(data.getPorcentajeice());
      retenciones.setMontoice(data.getMontoice());
      retenciones.setMontoivabienes(data.getMontoivabienes());
      retenciones.setCodretbienes(data.getCodretbienes());
      retenciones.setPorretbienes(data.getPorretbienes());
      retenciones.setValorretbienes(data.getValorretbienes());
      retenciones.setMontoivaservicios(data.getMontoivaservicios());
      retenciones.setCodretservicios(data.getCodretservicios());
      retenciones.setPorretservicios(data.getPorretservicios());
      retenciones.setValorretservicios(data.getValorretservicios());
      retenciones.setMontoivaserv100(data.getMontoivaserv100());
      retenciones.setCodretserv100(data.getCodretserv100());
      retenciones.setPorretserv100(data.getPorretserv100());
      retenciones.setValretserv100(data.getValretserv100());
      retenciones.setBaseimpair(data.getBaseimpair());
      retenciones.setCodretair(data.getCodretair());
      retenciones.setPorcentajeair(data.getPorcentajeair());
      retenciones.setValretair(data.getValretair());
      retenciones.setEstado(data.getEstado());
      retenciones.setNumautoriza(data.getNumautoriza());
      retenciones.setNumserie(data.getNumserie());
      retenciones.setFechacaduca(data.getFechacaduca());
      retenciones.setDescripcion(data.getDescripcion());
      retenciones.setIdbene(data.getIdbene());
      retenciones.setIddocu(data.getIddocu());
      retenciones.setIdautoriza(data.getIdautoriza());
      retenciones.setIdtabla01(data.getIdtabla01());
      retenciones.setIdtabla15(data.getIdtabla15());
      retenciones.setIdtabla5c_bie(data.getIdtabla5c_bie());
      retenciones.setIdtabla5c_ser(data.getIdtabla5c_ser());
      retenciones.setIdtabla5c_100(data.getIdtabla5c_100());
      retenciones.setClaveacceso(data.getClaveacceso());
      retenciones.setNumautoriza_e(data.getNumautoriza_e());
      retenciones.setFecautoriza(data.getFecautoriza());
      retenciones.setAutorizacion(data.getAutorizacion());
      retenciones.setIdtabla17(data.getIdtabla17());
      retenciones.setSecretencion1(data.getSecretencion1());
      retenciones.setFechaemiret1(data.getFechaemiret1());
      return save(retenciones);
   }

   // Elimina
   @Transactional
   public void deleteById(Long id) {
      Retenciones rete = dao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Retención no encontrada: " + id));
      Asientos asiento = rete.getIdasiento();
      if (asiento != null) {
         asiento.setSwretencion(0);
         daoAsientos.save(asiento);
      }
      dao.deleteById(id);
   }

}
