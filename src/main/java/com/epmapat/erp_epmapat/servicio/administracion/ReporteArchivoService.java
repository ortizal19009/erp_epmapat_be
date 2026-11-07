package com.epmapat.erp_epmapat.servicio.administracion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;

/** Archivos versionados: una actualizacion nunca sobreescribe la version en uso. */
@Service
public class ReporteArchivoService {
   private final Path root;

   public ReporteArchivoService(@Value("${app.reportesjr.directory:./reports}") String directory) {
      root = Path.of(directory).toAbsolutePath().normalize();
   }

   public byte[] leerJasper(Reportesjr reporte) throws IOException {
      if (reporte == null) throw new javax.persistence.EntityNotFoundException("No existe el reporte");
      if (reporte.getJasperPath() != null) return Files.readAllBytes(resolve(reporte.getJasperPath()));
      if (reporte.getJasper() != null) return reporte.getJasper(); // Compatibilidad durante migracion
      throw new IOException("El reporte no tiene archivo Jasper");
   }

   public void guardar(Reportesjr reporte, byte[] jrxml, byte[] jasper) throws IOException {
      Files.createDirectories(root);
      String version = UUID.randomUUID().toString();
      Path xml = resolve(version + ".jrxml");
      Path compiled = resolve(version + ".jasper");
      String anteriorXml = reporte.getJrxmlPath();
      String anteriorJasper = reporte.getJasperPath();
      try {
         Files.write(xml, jrxml);
         Files.write(compiled, jasper);
      } catch (IOException e) {
         borrar(xml); borrar(compiled);
         throw e;
      }
      if (!TransactionSynchronizationManager.isSynchronizationActive()) {
         borrar(xml); borrar(compiled);
         throw new IllegalStateException("Guardar archivos requiere una transaccion");
      }
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
         @Override public void afterCompletion(int status) {
            if (status == STATUS_COMMITTED) {
               borrarRuta(anteriorXml); borrarRuta(anteriorJasper);
            } else {
               borrar(xml); borrar(compiled);
            }
         }
      });
      reporte.setJrxmlPath(xml.getFileName().toString());
      reporte.setJasperPath(compiled.getFileName().toString());
      reporte.setJrxml(null);
      reporte.setJasper(null);
   }

   public void eliminarAlConfirmar(Reportesjr reporte) {
      String xml = reporte.getJrxmlPath(), jasper = reporte.getJasperPath();
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
         @Override public void afterCommit() { borrarRuta(xml); borrarRuta(jasper); }
      });
   }

   private Path resolve(String relative) throws IOException {
      Path path = root.resolve(relative).normalize();
      if (!path.startsWith(root) || Path.of(relative).isAbsolute())
         throw new IOException("Ruta de reporte invalida");
      if (Files.exists(path) && !path.toRealPath().startsWith(root.toRealPath()))
         throw new IOException("Ruta de reporte fuera del directorio configurado");
      return path;
   }

   private void borrarRuta(String path) {
      if (path == null) return;
      try { borrar(resolve(path)); }
      catch (IOException e) { org.slf4j.LoggerFactory.getLogger(getClass()).warn("No se pudo eliminar archivo de reporte", e); }
   }

   private void borrar(Path path) {
      try { Files.deleteIfExists(path); }
      catch (IOException e) { org.slf4j.LoggerFactory.getLogger(getClass()).warn("No se pudo eliminar {}", path, e); }
   }
}
