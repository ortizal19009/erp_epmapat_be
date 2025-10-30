package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Itemxfact;
import com.epmapat.erp_epmapat.repositorio.ItemxfactR;

@Service
public class ItemxfactServicio {

   @Autowired
   private ItemxfactR dao;

   // Productos de una Facturación
   public List<Itemxfact> getByIdfacturacion(Long idfacturacion) {
      return dao.findByIdfacturacion(idfacturacion);
   }

   //Movimientos de un Producto
   public List<Itemxfact> getByIdcatalogoitems(Long idcatalogoitems) {
      return dao.findByIdcatalogoitems(idcatalogoitems);
   }

   // Grabar
   public <S extends Itemxfact> S save(S entity) {
      return dao.save(entity);
   }

}
