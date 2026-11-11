package com.epmapat.erp_epmapat.sri.services;

import java.util.Date;
import java.util.Locale;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;

final class RetencionDatosEmision {
   private RetencionDatosEmision() {}

   static Date fecha(Retenciones retencion) {
      // fechaemision belongs to the supplier document; fechaemiret1 to the retention.
      return retencion.getFechaemiret1() != null ? retencion.getFechaemiret1() : retencion.getFechaemision();
   }

   static void validarRegeneracion(Retenciones retencion, Fec_retenciones electronica) {
      String estado = electronica == null || electronica.getEstado() == null ? ""
            : electronica.getEstado().trim().toUpperCase(Locale.ROOT).replace(' ', '_');
      String xml = electronica == null || electronica.getXmlautorizado() == null ? "" : electronica.getXmlautorizado();
      boolean autorizada = estado.equals("AUTORIZADA") || estado.equals("AUTORIZADO")
            || (xml.contains("<numeroAutorizacion>") && xml.contains("<estado>AUTORIZADO</estado>"))
            || (retencion.getNumautoriza_e() != null && !retencion.getNumautoriza_e().isBlank())
            || (retencion.getFecautoriza() != null && !retencion.getFecautoriza().isBlank());
      if (autorizada || (Integer.valueOf(1).equals(retencion.getEstado()) && !estado.equals("NO_AUTORIZADO"))) {
         throw new IllegalStateException("La retencion esta autorizada o requiere consultar su autorizacion antes de regenerar");
      }
   }
}
