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
@org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
public class ReportejrService {

   private final ReportesjrR dao;
   private final ReporteArchivoService archivos;
   private final com.epmapat.erp_epmapat.repositorio.administracion.RepoxopcionR opcionesDao;

   public byte[] leerJasper(Reportesjr reporte) throws IOException {
      return archivos.leerJasper(reporte);
   }

   public Reportesjr asignarOpciones(Short id, java.util.Set<Short> ids) {
      Reportesjr reporte = dao.findById(id).orElseThrow(() -> new EntityNotFoundException("No existe el reporte " + id));
      aplicarOpciones(reporte, ids);
      return dao.save(reporte);
   }

   private void aplicarOpciones(Reportesjr reporte, java.util.Set<Short> ids) {
      java.util.Set<Short> seleccion = new java.util.LinkedHashSet<>(ids);
      seleccion.remove(reporte.getRepoxopcion().getIdrepoxopcion());
      java.util.List<Repoxopcion> opciones = opcionesDao.findAllById(seleccion);
      if (opciones.size() != seleccion.size()) throw new IllegalArgumentException("Una opcion seleccionada no existe");
      reporte.setOpciones(new java.util.LinkedHashSet<>(opciones));
   }

   public Reportesjr migrarArchivos(Short id) throws IOException {
      Reportesjr reporte = dao.findById(id).orElseThrow(() -> new EntityNotFoundException("No existe el reporte " + id));
      if (reporte.getJasperPath() == null) {
         if (reporte.getJrxml() == null || reporte.getJasper() == null)
            throw new IllegalArgumentException("El reporte no tiene ambos archivos para migrar");
         archivos.guardar(reporte, reporte.getJrxml(), reporte.getJasper());
      }
      return dao.save(reporte);
   }

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
         MultipartFile jasper, java.util.Set<Short> opciones) throws JRException, IOException {
      Reportesjr reporte = new Reportesjr();
      reporte.setNomrep(nomrep);
      reporte.setMetodo(metodo);
      reporte.setDesrep(desrep);
      validarArchivos(jrxml, jasper);

      reporte.setRepoxopcion(opcionesDao.findById(idrepoxopcion).orElseThrow(() -> new IllegalArgumentException("La opcion no existe")));

      if (opciones != null) aplicarOpciones(reporte, opciones);
      JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxml.getBytes()));
      JsonNode parametrosJson = construirParametrosJson(jasperReport);
      reporte.setParametros(parametrosJson);
      archivos.guardar(reporte, jrxml.getBytes(), compilarBytes(jasperReport));

      return dao.save(reporte);
   }

   // Actualizar solo campos sin archivos
   public Reportesjr actualizarSoloCampos(Short id,
         Short idrepoxopcion,
         String nomrep,
         Short metodo,
         String desrep, java.util.Set<Short> opciones) {
      Reportesjr reporte = dao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe el reporte con id " + id));

      reporte.setNomrep(nomrep);
      reporte.setMetodo(metodo);
      reporte.setDesrep(desrep);

      reporte.setRepoxopcion(opcionesDao.findById(idrepoxopcion).orElseThrow(() -> new IllegalArgumentException("La opcion no existe")));

      if (opciones != null) aplicarOpciones(reporte, opciones);
      return dao.save(reporte);
   }

   // Actualizar todos los campos incluyendo archivos
   public Reportesjr actualizarCompleto(Short id,
         Short idrepoxopcion,
         String nomrep,
         Short metodo,
         String desrep,
         MultipartFile jrxml,
         MultipartFile jasper, java.util.Set<Short> opciones) throws JRException, IOException {
      Reportesjr reporte = dao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("No existe el reporte con id " + id));
      // Metadatos
      reporte.setNomrep(nomrep);
      reporte.setMetodo(metodo);
      reporte.setDesrep(desrep);
      reporte.setRepoxopcion(opcionesDao.findById(idrepoxopcion).orElseThrow(() -> new IllegalArgumentException("La opcion no existe")));
      // Archivos
      validarArchivos(jrxml, jasper);

      if (opciones != null) aplicarOpciones(reporte, opciones);
      JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxml.getBytes()));

      JsonNode parametrosJson = construirParametrosJson(jasperReport);
      reporte.setParametros(parametrosJson);
      archivos.guardar(reporte, jrxml.getBytes(), compilarBytes(jasperReport));

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
      Reportesjr reporte = dao.findById(idreporte).orElseThrow(() -> new EntityNotFoundException("No existe el reporte " + idreporte));
      archivos.eliminarAlConfirmar(reporte);
      dao.delete(reporte);
   }

   private byte[] compilarBytes(JasperReport report) throws JRException {
      java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
      net.sf.jasperreports.engine.util.JRSaver.saveObject(report, output);
      return output.toByteArray();
   }

   private void validarArchivos(MultipartFile jrxml, MultipartFile jasper) {
      if (jrxml == null || jrxml.isEmpty() || jasper == null || jasper.isEmpty())
         throw new IllegalArgumentException("Debe seleccionar JRXML y Jasper");
      String xmlName = jrxml.getOriginalFilename(), jasperName = jasper.getOriginalFilename();
      if (xmlName == null || jasperName == null || !xmlName.toLowerCase().endsWith(".jrxml")
            || !jasperName.toLowerCase().endsWith(".jasper")
            || !xmlName.substring(0, xmlName.length() - 6).equals(jasperName.substring(0, jasperName.length() - 7)))
         throw new IllegalArgumentException("Los archivos JRXML y Jasper deben tener el mismo nombre base");
      // Se guarda el compilado del JRXML para que los parametros y el ejecutable coincidan.
   }

}
