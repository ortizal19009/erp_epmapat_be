package com.epmapat.erp_epmapat.servicio.administracion;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;

class ReporteArchivoServiceTest {
   @TempDir Path root;

   @AfterEach void limpiarTransaccion() {
      if (TransactionSynchronizationManager.isSynchronizationActive())
         TransactionSynchronizationManager.clearSynchronization();
   }

   private void completar(int status) {
      TransactionSynchronizationManager.getSynchronizations().forEach(s -> s.afterCompletion(status));
      TransactionSynchronizationManager.clearSynchronization();
   }

   @Test void guardaEnDiscoYEliminaBinariosSoloDeLaEntidad() throws Exception {
      ReporteArchivoService service = new ReporteArchivoService(root.toString());
      Reportesjr reporte = new Reportesjr();
      reporte.setJasper(new byte[]{9});
      TransactionSynchronizationManager.initSynchronization();
      service.guardar(reporte, new byte[]{1}, new byte[]{2});
      completar(TransactionSynchronization.STATUS_COMMITTED);
      assertNull(reporte.getJasper());
      assertNull(reporte.getJrxml());
      assertArrayEquals(new byte[]{2}, service.leerJasper(reporte));
      assertTrue(Files.exists(root.resolve(reporte.getJrxmlPath())));
   }

   @Test void rollbackConservaVersionAnteriorYBorraNueva() throws Exception {
      ReporteArchivoService service = new ReporteArchivoService(root.toString());
      Reportesjr reporte = new Reportesjr();
      Files.write(root.resolve("anterior.jasper"), new byte[]{8});
      reporte.setJasperPath("anterior.jasper");
      TransactionSynchronizationManager.initSynchronization();
      service.guardar(reporte, new byte[]{1}, new byte[]{2});
      String nuevo = reporte.getJasperPath();
      completar(TransactionSynchronization.STATUS_ROLLED_BACK);
      assertFalse(Files.exists(root.resolve(nuevo)));
      assertTrue(Files.exists(root.resolve("anterior.jasper")));
   }

   @Test void commitBorraVersionAnterior() throws Exception {
      ReporteArchivoService service = new ReporteArchivoService(root.toString());
      Reportesjr reporte = new Reportesjr();
      Files.write(root.resolve("anterior.jasper"), new byte[]{8});
      reporte.setJasperPath("anterior.jasper");
      TransactionSynchronizationManager.initSynchronization();
      service.guardar(reporte, new byte[]{1}, new byte[]{2});
      completar(TransactionSynchronization.STATUS_COMMITTED);
      assertFalse(Files.exists(root.resolve("anterior.jasper")));
      assertArrayEquals(new byte[]{2}, service.leerJasper(reporte));
   }

   @Test void permiteLegadoYRechazaRutasFueraDeCarpeta() throws Exception {
      ReporteArchivoService service = new ReporteArchivoService(root.toString());
      Reportesjr reporte = new Reportesjr();
      reporte.setJasper(new byte[]{7});
      assertArrayEquals(new byte[]{7}, service.leerJasper(reporte));
      reporte.setJasperPath("../otro.jasper");
      assertThrows(java.io.IOException.class, () -> service.leerJasper(reporte));
   }
}
