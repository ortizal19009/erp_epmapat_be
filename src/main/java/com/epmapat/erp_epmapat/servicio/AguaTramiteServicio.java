package com.epmapat.erp_epmapat.servicio;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.AguaTramite;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.repositorio.AguaTramiteR;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Service

public class AguaTramiteServicio {

   @Autowired
   private AguaTramiteR dao;

   public List<AguaTramite> findAll(Long desde, Long hasta) {
      if (desde != null || hasta != null) {
         return dao.findAll(desde, hasta);
      } else {
         return dao.findAll();
      }
   }

   public List<AguaTramite> findByIdTipTramite(Long idtipotramite, Long estado, Date d, Date h) {
      return dao.findByIdTipTramite(idtipotramite, estado, d, h, d, h);
   }

   public List<AguaTramite> findByNombre(String nombre) {
      return dao.findByCliente(nombre);
   }

   public Page<AguaTramite> buscarPageable(Long idtipotramite, Integer estado, String cliente, Date fechaDesde, Date fechaHasta, int page, int size) {
      Pageable pageable = PageRequest.of(
            Math.max(page, 0),
            Math.max(size, 1),
            Sort.by(Sort.Order.desc("feccrea"), Sort.Order.desc("idaguatramite")));
      String clienteLimpio = limpiar(cliente);
      Date fechaHastaExclusive = sumarUnDia(fechaHasta);

      Specification<AguaTramite> specification = (root, query, cb) -> {
         List<Predicate> predicates = new ArrayList<>();
         predicates.add(cb.equal(root.get("idtipotramite_tipotramite").get("idtipotramite"), idtipotramite));

         if (estado != null) {
            predicates.add(cb.equal(root.get("estado"), estado));
         }

         if (clienteLimpio != null) {
            Join<AguaTramite, Clientes> clienteJoin = root.join("idcliente_clientes");
            predicates.add(cb.like(
                  cb.lower(cb.coalesce(clienteJoin.get("nombre"), "")),
                  "%" + clienteLimpio.toLowerCase() + "%"));
         }

         if (fechaDesde != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("feccrea"), fechaDesde));
         }

         if (fechaHastaExclusive != null) {
            predicates.add(cb.lessThan(root.get("feccrea"), fechaHastaExclusive));
         }

         return cb.and(predicates.toArray(new Predicate[0]));
      };

      return dao.findAll(specification, pageable);
   }

   public Optional<AguaTramite> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends AguaTramite> S save(S entity) {
		return dao.save(entity);
	}

   public void deleteById(Long id) {
		dao.deleteById(id);
	}

   private String limpiar(String valor) {
      if (valor == null || valor.trim().isEmpty()) {
         return null;
      }
      return valor.trim();
   }

   private Date sumarUnDia(Date fecha) {
      if (fecha == null) {
         return null;
      }
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(fecha);
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      return calendar.getTime();
   }

}
