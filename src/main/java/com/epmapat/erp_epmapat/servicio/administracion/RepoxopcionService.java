package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Repoxopcion;
import com.epmapat.erp_epmapat.repositorio.administracion.RepoxopcionR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class RepoxopcionService {

   private final RepoxopcionR dao;

   // Busca Repoxopcion
   public List<Repoxopcion> buscaRepoxopcion(String codigo, String opcion, String nombre) {
      return dao.findByCodigoStartingWithAndOpcionContainingIgnoreCaseAndNombreContainingIgnoreCaseOrderByCodigoAsc(
            codigo, opcion, nombre);
   }

   // Repoxopcion para datalist
   public List<Repoxopcion> datalistRepoxopcion(String prefijo) {
      return dao.findByCodigoStartingWithOrderByCodigo(prefijo);
   }

   // Repoxopcion por Código y Largo
   public List<Repoxopcion> obtenerPorPrefijoYLargo(String prefijo) {
      return dao.findByCodigoStartingWithAndLength(prefijo, prefijo.length() + 2);
   }

   // Valida codigo
   public boolean valCodigo(String codigo) {
      return dao.existsByCodigo(codigo);
   }

   // Validar nombre
   public boolean valNombre(String nombre) {
      return dao.existsByNombreIgnoreCase(nombre);
   }

   // Guarda nuevo
   public <S extends Repoxopcion> S save(S entity) {
      return dao.save(entity);
   }

   // Actualiza
   public Repoxopcion actualizar(Short idrepoxopcion, Repoxopcion x) {
      Optional<Repoxopcion> y = dao.findById(idrepoxopcion);
      if (y.isPresent()) {
         Repoxopcion repox = y.get();
         repox.setCodigo(x.getCodigo());
         repox.setOpcion(x.getOpcion());
         repox.setNombre(x.getNombre());

         repox.setUsumodi(x.getUsumodi());
         repox.setFecmodi(x.getFecmodi());

         return dao.save(repox);
      } else {
         throw new RuntimeException("Repoxopcion no encontrado con id " + idrepoxopcion);
      }
   }

   // Elimina
   public void deleteById(Short idrepoxopcion) {
      dao.deleteById(idrepoxopcion);
   }

}
