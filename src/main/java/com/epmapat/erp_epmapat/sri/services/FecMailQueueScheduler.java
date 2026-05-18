package com.epmapat.erp_epmapat.sri.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.servicio.FecMailQueueService;

@Service
public class FecMailQueueScheduler {

   private final FecMailQueueService queueService;

   public FecMailQueueScheduler(FecMailQueueService queueService) {
      this.queueService = queueService;
   }

   @Scheduled(fixedDelayString = "${sri.facturas.queue-delay-ms:10000}")
   public void procesarColaFacturas() {
      queueService.procesarPendientes();
      queueService.sincronizarEstadosOutbox();
   }
}
