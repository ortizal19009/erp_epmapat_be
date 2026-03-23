package com.epmapat.erp_epmapat.servicio.administracion;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;
import com.epmapat.erp_epmapat.modelo.administracion.Repoxopcion;
import com.epmapat.erp_epmapat.repositorio.administracion.ReportesjrR;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import java.io.ByteArrayInputStream;

@RequiredArgsConstructor
@Service
public class ReportejrService {

   private final ReportesjrR dao;

   // Buscar reportesjr por nombre y desrep ordenados por codrep
   public List<Reportesjr> buscarPorOpcionNomrepYDesrep(String codigo, String nomrep, String desrep) {
      return dao
            .findByRepoxopcion_CodigoStartingWithAndNomrepContainingIgnoreCaseAndDesrepContainingIgnoreCaseOrderByRepoxopcion_CodigoAscNomrepAsc(
                  codigo, nomrep, desrep);
   }

   // Reportes de un nomrep específico
   public List<Reportesjr> obtenerReportesPorNomrep(String nomrepBase) {
      return dao.findByNomrepStartingWithOrderByNomrep(nomrepBase);
   }

   // Buscar todos los reportes con un Repoxopcion_Codigo específico
   public List<Reportesjr> buscaPorRepoxopcion_CodigoOrderByNomrep(String codigo) {
      return dao.findByRepoxopcion_CodigoAndMetodoLessThanOrderByNomrep(codigo,  (short)3);
   }

   // Valida nomrep
   public boolean valNomrep(String nomrep) {
      return dao.existsByNomrep(nomrep);
   }

   // Conteo por idrepoxopcion
   public short cuentaPorRepoxopcion(short idrepoxopcion) {
      return dao.countByRepoxopcion_Idrepoxopcion(idrepoxopcion);
   }

   // Busca por nomrep
   public Reportesjr findByNomrep(String nomrep) {
      return dao.findByNomrep(nomrep);
   }

   // Crear un nuevo reporte con archivos jrxml y jasper
   public Reportesjr crearReporte(Short idrepoxopcion,
         String nomrep,
         Short metodo,
         String desrep,
         MultipartFile jrxml,
         MultipartFile jasper) throws JRException, IOException {
      Reportesjr reporte = new Reportesjr();
      reporte.setNomrep(nomrep);
      reporte.setMetodo(metodo);
      reporte.setDesrep(desrep);
      reporte.setJrxml(jrxml.getBytes());
      reporte.setJasper(jasper.getBytes());

      Repoxopcion repoxopcion = new Repoxopcion();
      repoxopcion.setIdrepoxopcion(idrepoxopcion);
      reporte.setRepoxopcion(repoxopcion);

      JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxml.getBytes()));
      JsonNode parametrosJson = construirParametrosJson(jasperReport);
      reporte.setParametros(parametrosJson);

      return dao.save(reporte);
   }

   // Actualizar solo campos sin archivos
   public Reportesjr actualizarSoloCampos(Short id,
         Short idrepoxopcion,
         String nomrep,
         Short metodo,
         String desrep) {
      Reportesjr reporte = dao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe el reporte con id " + id));

      reporte.setNomrep(nomrep);
      reporte.setMetodo(metodo);
      reporte.setDesrep(desrep);

      Repoxopcion repoxopcion = new Repoxopcion();
      repoxopcion.setIdrepoxopcion(idrepoxopcion);
      reporte.setRepoxopcion(repoxopcion);

      return dao.save(reporte);
   }

   // Actualizar todos los campos incluyendo archivos
   public Reportesjr actualizarCompleto(Short id,
         Short idrepoxopcion,
         String nomrep,
         Short metodo,
         String desrep,
         MultipartFile jrxml,
         MultipartFile jasper) throws JRException, IOException {
      Reportesjr reporte = dao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe el reporte con id " + id));
      // Metadatos
      reporte.setNomrep(nomrep);
      reporte.setMetodo(metodo);
      reporte.setDesrep(desrep);
      reporte.setRepoxopcion(new Repoxopcion(idrepoxopcion));
      // Archivos
      reporte.setJrxml(jrxml.getBytes());
      reporte.setJasper(jasper.getBytes());

      JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxml.getBytes()));

      JsonNode parametrosJson = construirParametrosJson(jasperReport);
      reporte.setParametros(parametrosJson);

      return dao.save(reporte);
   }

   // Extrae los parámetros del JasperReport Usando JsonNode
   public JsonNode construirParametrosJson(JasperReport jasperReport) {
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode parametrosJson = mapper.createObjectNode();
      Arrays.stream(jasperReport.getParameters())
            .filter(p -> !p.isSystemDefined())
            .forEach(p -> {
               String nombre = p.getName();
               String tipo = p.getValueClassName();
               String anchoCampo = Optional.ofNullable(p.getPropertiesMap().getProperty("anchoCampo")).orElse("12");
               String orden = Optional.ofNullable(p.getPropertiesMap().getProperty("orden")).orElse("999");

               parametrosJson.put(nombre, tipo + "|" + anchoCampo + "|" + orden);
            });
      return parametrosJson;
   }

   // Busca un reportejr por ID
   public Optional<Reportesjr> buscaPorId(Short id) {
      return dao.findById(id);
   }

   public void deleteById(Short idreporte) {
      dao.deleteById(idreporte);
   }

}
