package com.epmapat.erp_epmapat.sri.services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;

@Service
public class RetencionPendienteScheduler {

   private final RetencionSRIService retencionSRIService;
   private final RetencionProcesamientoSRIService retencionProcesamientoSRIService;

   public RetencionPendienteScheduler(
         RetencionSRIService retencionSRIService,
         RetencionProcesamientoSRIService retencionProcesamientoSRIService) {
      this.retencionSRIService = retencionSRIService;
      this.retencionProcesamientoSRIService = retencionProcesamientoSRIService;
   }

   @Scheduled(fixedDelayString = "${sri.retenciones.pending-check-delay-ms:60000}")
   public void revisarPendientes() {
      List<Fec_retenciones> pendientes = retencionSRIService.listarPorEstado("PENDIENTE_AUTORIZACION");
      for (Fec_retenciones pendiente : pendientes) {
         if (pendiente == null || pendiente.getIdretencion() == null) {
            continue;
         }
         try {
            retencionProcesamientoSRIService.consultarEstadoPendiente(pendiente.getIdretencion());
         } catch (Exception ignored) {
            // El scheduler vuelve a intentar en el siguiente ciclo.
         }
      }
   }
}
