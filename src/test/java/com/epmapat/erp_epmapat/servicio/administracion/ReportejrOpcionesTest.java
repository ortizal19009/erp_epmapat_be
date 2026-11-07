package com.epmapat.erp_epmapat.servicio.administracion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.modelo.administracion.*;
import com.epmapat.erp_epmapat.repositorio.administracion.*;

class ReportejrOpcionesTest {
   @Test void conservaPrincipalYGuardaOpcionesAdicionalesSinDuplicados() {
      ReportesjrR dao = mock(ReportesjrR.class);
      RepoxopcionR opciones = mock(RepoxopcionR.class);
      ReportejrService service = new ReportejrService(dao, mock(ReporteArchivoService.class), opciones);
      Reportesjr reporte = new Reportesjr();
      reporte.setRepoxopcion(new Repoxopcion((short) 1));
      Repoxopcion adicional = new Repoxopcion((short) 2);
      when(dao.findById((short) 10)).thenReturn(Optional.of(reporte));
      when(opciones.findAllById(Collections.singleton((short) 2))).thenReturn(Collections.singletonList(adicional));
      when(dao.save(reporte)).thenReturn(reporte);
      service.asignarOpciones((short) 10, new LinkedHashSet<>(Arrays.asList((short) 1, (short) 2)));
      assertEquals(1, reporte.getRepoxopcion().getIdrepoxopcion());
      assertEquals(Collections.singleton(adicional), reporte.getOpciones());
   }

   @Test void rechazaOpcionesInexistentesAntesDeGuardar() {
      ReportesjrR dao = mock(ReportesjrR.class);
      RepoxopcionR opciones = mock(RepoxopcionR.class);
      ReportejrService service = new ReportejrService(dao, mock(ReporteArchivoService.class), opciones);
      Reportesjr reporte = new Reportesjr();
      reporte.setRepoxopcion(new Repoxopcion((short) 1));
      when(dao.findById((short) 10)).thenReturn(Optional.of(reporte));
      assertThrows(IllegalArgumentException.class,
            () -> service.asignarOpciones((short) 10, Collections.singleton((short) 99)));
      verify(dao, never()).save(any());
   }
}
