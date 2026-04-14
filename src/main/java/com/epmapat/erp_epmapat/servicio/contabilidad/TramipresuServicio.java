package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EjecucioR;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TramipresuR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramipresuServicio {

   private final TramipresuR dao;
   private final EjecucioR ejecucioDao;

   public Tramipresu findFirstByOrderByNumeroDesc() {
      return dao.findFirstByOrderByNumeroDesc();
   }

   // Validar número de Trámite
   public boolean valNumero(Long numero) {
      return dao.existsByNumero(numero);
   }

   // Un Trámite por Número
   public Tramipresu buscaPorNumero(Long numero) {
      return dao.findByNumero(numero)
            .orElse(null);
   }

   public List<Tramipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
      return dao.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
   }

   public Optional<Tramipresu> findById(Long id) {
      return dao.findById(id);
   }

   // Actualiza tramipresu.totmiso
   // @Transactional ya se usa en la llamad
   public void actualizaTotmiso(Long idtrami) {
      BigDecimal totmiso = ejecucioDao.sumaPrmisoPorIdtrami(idtrami);
      Tramipresu trami = dao.findById(idtrami)
            .orElseThrow(() -> new IllegalArgumentException("No existe Tramipresu con id " + idtrami));
      trami.setTotmiso(totmiso);
      dao.save(trami);
   }

   // Guarda (recibe dto)
   public Tramipresu save(Tramipresu nueva) {
      return dao.save(nueva);
   }

   // Actualiza solo los modificados con Patch
   public Tramipresu updateTramipresu(Long idtrami, Tramipresu data) {
      Tramipresu tramipresu = findById(idtrami)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  "No se encuentra este Id " + idtrami));
      // Coloca campos (solo los modificados)
      if (data.getNumero() != null)
         tramipresu.setNumero(data.getNumero());
      if (data.getFecha() != null)
         tramipresu.setFecha(data.getFecha());
      if (data.getIntdoc() != null)
         tramipresu.setIntdoc(data.getIntdoc());
      if (data.getNumdoc() != null)
         tramipresu.setNumdoc(data.getNumdoc());
      if (data.getFecdoc() != null)
         tramipresu.setFecdoc(data.getFecdoc());
      if (data.getIdbene() != null)
         tramipresu.setIdbene(data.getIdbene());
      if (data.getDescri() != null)
         tramipresu.setDescri(data.getDescri());
      tramipresu.setUsumodi(data.getUsumodi());
      tramipresu.setFecmodi(data.getFecmodi());
      return save(tramipresu);
   }

   // Antes de eliminar busca (otro usuario pudo eliminar)
   public boolean deleteById(Long idtrami) {
      return dao.findById(idtrami)
            .map(entity -> {
               dao.deleteById(idtrami);
               return true; // sí existía y se eliminó
            })
            .orElse(false); // no existía
   }

}
