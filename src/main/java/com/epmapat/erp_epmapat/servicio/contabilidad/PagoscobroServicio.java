package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Pagoscobros;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PagoscobrosR;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PagoscobroServicio {

   private final PagoscobrosR dao;
   private final BenexTranServicio benxtraServicio;

   // Pagoscobros de una benextran.idbenxtra
   public List<Pagoscobros> findByIdbenxtra(Long idbenxtra) {
      return dao.findByIdbenxtra_Idbenxtra(idbenxtra);
   }

   // Pagoscobros de una transaci.inttra
   public List<Pagoscobros> findByInttra(Long inttra) {
      return dao.findByInttra_Inttra(inttra);
   }

   // Pagoscobros por idpagcob
   public Optional<Pagoscobros> findById(Long id) {
      return dao.findById(id);
   }

   // Guarda nueva y actualiza benextran.totpagcob
   @Transactional
   public Pagoscobros savePagocobro(Pagoscobros nuevo) {
      Pagoscobros guardado = dao.save(nuevo);
      benxtraServicio.actualizaTotPagcob(
            guardado.getIdbenxtra().getIdbenxtra());
      return guardado;
   }

   // Actualiza (solo se puede modificar valor) Too actualiza benextran.totpagcob
   @Transactional
   public Pagoscobros updatePagoscobros(Long idpagcob, Pagoscobros data) {
      Pagoscobros pagcob = findById(idpagcob)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  "No se encuentra este Id " + idpagcob));
      if (data.getValor() != null) {
         pagcob.setValor(data.getValor());
      }
      Pagoscobros actualizado = dao.save(pagcob);
      benxtraServicio.actualizaTotPagcob(
            actualizado.getIdbenxtra().getIdbenxtra());
      return actualizado;
   }

   // Antes de eliminar busca (otro usuario pudo eliminar)
   @Transactional
   public boolean deleteById(Long idpagcob) {
      Optional<Pagoscobros> opt = dao.findById(idpagcob);
      if (opt.isEmpty()) {
         return false; // No existe → 204 No Content
      }
      // Si existe => elimina y actualiza benextran.totpagcob
      Pagoscobros pagcob = opt.get();
      Long idbenxtra = pagcob.getIdbenxtra().getIdbenxtra();
      dao.delete(opt.get());
      // Recalcular benextran.totpagcob
      benxtraServicio.actualizaTotPagcob(idbenxtra);
      return true; // Eliminado → 200 OK
   }

}
