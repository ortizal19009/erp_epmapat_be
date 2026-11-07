package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;

public interface ReportesjrR extends JpaRepository<Reportesjr, Short> {

   @Override
   @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"repoxopcion", "opciones"})
   java.util.Optional<Reportesjr> findById(Short id);


   // Buscar reportesjr por opcion, nombre y desrep ordenados por codrep
   @org.springframework.data.jpa.repository.Query("select distinct r from Reportesjr r where (r.repoxopcion.codigo like concat(:codigo, '%') or exists (select o.idrepoxopcion from r.opciones o where o.codigo like concat(:codigo, '%'))) and lower(r.nomrep) like lower(concat('%', :nomrep, '%')) and lower(r.desrep) like lower(concat('%', :desrep, '%')) order by r.repoxopcion.codigo, r.nomrep")
   @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"repoxopcion", "opciones"})
   @org.springframework.data.jpa.repository.QueryHints(@javax.persistence.QueryHint(name = "hibernate.query.passDistinctThrough", value = "false"))
   List<Reportesjr> findByRepoxopcion_CodigoStartingWithAndNomrepContainingIgnoreCaseAndDesrepContainingIgnoreCaseOrderByRepoxopcion_CodigoAscNomrepAsc(
         @org.springframework.data.repository.query.Param("codigo") String codigo, @org.springframework.data.repository.query.Param("nomrep") String nomrep, @org.springframework.data.repository.query.Param("desrep") String desrep);

   // Reportes de un nomrep específico
   @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"repoxopcion", "opciones"})
   @org.springframework.data.jpa.repository.QueryHints(@javax.persistence.QueryHint(name = "hibernate.query.passDistinctThrough", value = "false"))
   @org.springframework.data.jpa.repository.Query("select distinct r from Reportesjr r where r.nomrep like concat(:nomrep, '%') order by r.nomrep")
   List<Reportesjr> findByNomrepStartingWithOrderByNomrep(@org.springframework.data.repository.query.Param("nomrep") String nomrepBase);

   // Busca un Reporte por nomrep,
   @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"repoxopcion", "opciones"})
   Reportesjr findByNomrep(String nomrep);

   // Valida si existe un nomrep
   boolean existsByNomrep(String nomrep);

   // Buscar todos los reportes de un Repoxopcion_Codigo específico (sin los de tipo 3)
   @org.springframework.data.jpa.repository.Query("select distinct r from Reportesjr r where (r.repoxopcion.codigo = :codigo or exists (select o.idrepoxopcion from r.opciones o where o.codigo = :codigo)) and r.metodo < :metodo order by r.nomrep")
   @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"repoxopcion", "opciones"})
   @org.springframework.data.jpa.repository.QueryHints(@javax.persistence.QueryHint(name = "hibernate.query.passDistinctThrough", value = "false"))
   List<Reportesjr> findByRepoxopcion_CodigoAndMetodoLessThanOrderByNomrep(@org.springframework.data.repository.query.Param("codigo") String codigo, @org.springframework.data.repository.query.Param("metodo") Short metodo);

   // Conteo por idrepoxopcion
   @org.springframework.data.jpa.repository.Query("select count(r) from Reportesjr r where r.repoxopcion.idrepoxopcion = :id or exists (select o.idrepoxopcion from r.opciones o where o.idrepoxopcion = :id)")
   short countByRepoxopcion_Idrepoxopcion(@org.springframework.data.repository.query.Param("id") short idrepoxopcion);

}
