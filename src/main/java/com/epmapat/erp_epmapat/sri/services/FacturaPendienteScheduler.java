package com.epmapat.erp_epmapat.sri.services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.servicio.Fec_facturaService;

@Service
public class FacturaPendienteScheduler {
   private static final int LOTE_PENDIENTES = 25;

   private final Fec_facturaService fecFacturaService;
   private final FacturaPostAuthorizationService postAuthorizationService;

   public FacturaPendienteScheduler(Fec_facturaService fecFacturaService,
         FacturaPostAuthorizationService postAuthorizationService) {
      this.fecFacturaService = fecFacturaService;
      this.postAuthorizationService = postAuthorizationService;
   }

   @Scheduled(fixedDelayString = "${sri.facturas.pending-check-delay-ms:300000}")
   public void reprocesarPendientesAutorizacion() {
      List<Fec_factura> pendientes = fecFacturaService.findPendientesAutorizacion(LOTE_PENDIENTES);
      for (Fec_factura factura : pendientes) {
         fecFacturaService.recuperarXmlAutorizado(factura)
               .ifPresent(postAuthorizationService::procesarFacturaAutorizada);
      }
   }
}
