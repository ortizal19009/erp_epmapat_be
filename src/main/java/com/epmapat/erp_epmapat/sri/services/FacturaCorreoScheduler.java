package com.epmapat.erp_epmapat.sri.services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.servicio.Fec_facturaService;

@Service
public class FacturaCorreoScheduler {
   private static final int LOTE_CORREOS = 25;

   private final Fec_facturaService fecFacturaService;
   private final FacturaPostAuthorizationService postAuthorizationService;

   public FacturaCorreoScheduler(Fec_facturaService fecFacturaService,
         FacturaPostAuthorizationService postAuthorizationService) {
      this.fecFacturaService = fecFacturaService;
      this.postAuthorizationService = postAuthorizationService;
   }

   @Scheduled(fixedDelayString = "${sri.facturas.mail-delay-ms:300000}")
   public void enviarAutorizadasPendientes() {
      List<Fec_factura> facturas = fecFacturaService.findListasParaCorreo(LOTE_CORREOS);
      for (Fec_factura factura : facturas) {
         postAuthorizationService.procesarFacturaAutorizada(factura);
      }
   }
}
