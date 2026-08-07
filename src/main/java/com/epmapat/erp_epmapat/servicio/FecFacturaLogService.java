package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Fec_factura_log;
import com.epmapat.erp_epmapat.repositorio.Fec_factura_logR;

@Service
public class FecFacturaLogService {

   private final Fec_factura_logR repository;

   public FecFacturaLogService(Fec_factura_logR repository) {
      this.repository = repository;
   }

   public void registrar(Long idfactura, String estado, String mensaje) {
      Fec_factura_log log = new Fec_factura_log();
      log.setIdfactura(idfactura);
      log.setEstado(estado);
      log.setMensaje(mensaje);
      log.setFecha(LocalDateTime.now());
      repository.save(log);
   }

   public List<Fec_factura_log> listarPorFactura(Long idfactura) {
      return repository.findByIdfacturaOrderByFechaAsc(idfactura);
   }

   public void registrarReasignacion(Long idfactura, String mensaje) {
      registrar(idfactura, "R", mensaje);
   }
}
