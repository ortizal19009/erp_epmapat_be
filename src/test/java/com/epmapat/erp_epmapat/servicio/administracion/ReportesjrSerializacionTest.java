package com.epmapat.erp_epmapat.servicio.administracion;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import javax.persistence.EntityManager;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import com.epmapat.erp_epmapat.modelo.administracion.*;
import com.epmapat.erp_epmapat.repositorio.administracion.ReportesjrR;
import com.epmapat.erp_epmapat.config.JacksonHibernateConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Ejecutar exclusivamente con una base temporal: crea y elimina estas tablas. */
@EnabledIfEnvironmentVariable(named = "REPORTESJR_TEST_JDBC_URL", matches = ".+")
class ReportesjrSerializacionTest {
   @Test void incluyeOpcionesFueraDeLaSesionSinDuplicarReportes() throws Exception {
      Configuration cfg = new Configuration().addAnnotatedClass(Reportesjr.class).addAnnotatedClass(Repoxopcion.class);
      cfg.setProperty("hibernate.connection.url", System.getenv("REPORTESJR_TEST_JDBC_URL"));
      cfg.setProperty("hibernate.connection.username", "catalogo_test");
      cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
      cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
      cfg.setProperty("hibernate.show_sql", "false");
      try (var factory = cfg.buildSessionFactory()) {
         EntityManager em = factory.createEntityManager();
         em.getTransaction().begin();
         Repoxopcion principal = opcion("99001", "Reportes JR");
         Repoxopcion adicional = opcion("99002", "Recaudacion");
         Repoxopcion otra = opcion("99003", "Emisiones");
         em.persist(principal); em.persist(adicional); em.persist(otra);
         Reportesjr reporte = new Reportesjr();
         reporte.setNomrep("Prueba"); reporte.setDesrep("Prueba de serializacion"); reporte.setMetodo((short) 1);
         reporte.setRepoxopcion(principal);
         reporte.setParametros(new ObjectMapper().readTree("{\"idusuario\":\"java.lang.Integer|12|1\"}"));
         reporte.setOpciones(new LinkedHashSet<>(Arrays.asList(adicional, otra)));
         em.persist(reporte);
         em.getTransaction().commit(); em.clear();

         ReportesjrR repository = new JpaRepositoryFactory(em).getRepository(ReportesjrR.class);
         List<Reportesjr> resultados = repository
               .findByRepoxopcion_CodigoStartingWithAndNomrepContainingIgnoreCaseAndDesrepContainingIgnoreCaseOrderByRepoxopcion_CodigoAscNomrepAsc("", "", "");
         assertEquals(1, resultados.size(), "Dos opciones adicionales no deben duplicar el reporte");
         em.clear();
         assertEquals(1, repository.findByNomrepStartingWithOrderByNomrep("P").size());
         em.clear();
         assertEquals(1, repository.findByRepoxopcion_CodigoAndMetodoLessThanOrderByNomrep("99002", (short) 3).size());
         em.clear();
         Reportesjr detalle = repository.findById(reporte.getIdreporte()).orElseThrow();
         em.close();

         ObjectMapper mapper = new ObjectMapper().registerModule(new JacksonHibernateConfig().hibernate5Module());
         for (Reportesjr resultado : Arrays.asList(resultados.get(0), detalle)) {
            var json = mapper.readTree(mapper.writeValueAsString(resultado));
            assertEquals("99001", json.path("repoxopcion").path("codigo").asText());
            assertEquals("Reportes JR", json.path("repoxopcion").path("opcion").asText());
            assertEquals(2, json.path("opciones").size());
         }
      }
   }

   private Repoxopcion opcion(String codigo, String nombre) {
      Repoxopcion opcion = new Repoxopcion();
      opcion.setCodigo(codigo); opcion.setOpcion(nombre); opcion.setNombre(nombre);
      return opcion;
   }
}
