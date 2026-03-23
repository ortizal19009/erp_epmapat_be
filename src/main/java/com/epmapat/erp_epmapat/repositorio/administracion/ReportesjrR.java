package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;

public interface ReportesjrR extends JpaRepository<Reportesjr, Short> {

   // Buscar reportesjr por opcion, nombre y desrep ordenados por codrep
   List<Reportesjr> findByRepoxopcion_CodigoStartingWithAndNomrepContainingIgnoreCaseAndDesrepContainingIgnoreCaseOrderByRepoxopcion_CodigoAscNomrepAsc(
         String codigo, String nomrep, String desrep);

   // Reportes de un nomrep específico
   List<Reportesjr> findByNomrepStartingWithOrderByNomrep(String nomrepBase);

   // Busca un Reporte por nomrep,
   Reportesjr findByNomrep(String nomrep);

   // Valida si existe un nomrep
   boolean existsByNomrep(String nomrep);

   // Buscar todos los reportes de un Repoxopcion_Codigo específico (sin los de tipo 3)
   List<Reportesjr> findByRepoxopcion_CodigoAndMetodoLessThanOrderByNomrep(String codigo, Short metodo);

   // Conteo por idrepoxopcion
   short countByRepoxopcion_Idrepoxopcion(short idrepoxopcion);

}
