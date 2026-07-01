package com.epmapat.erp_epmapat.servicio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.FacturacionCuotasPendientesDTO;
import com.epmapat.erp_epmapat.modelo.Facturacion;
import com.epmapat.erp_epmapat.modelo.Liquidafac;
import com.epmapat.erp_epmapat.repositorio.FacturacionR;
import com.epmapat.erp_epmapat.repositorio.LiquidafacR;

@Service
public class FacturacionServicio {

   @Autowired
   private FacturacionR dao;

   @Autowired
   private LiquidafacR liquidafacR;

   public List<Facturacion> findDesdeHasta(Long desde, Long hasta, Date del, Date al) {
      if (desde != null && hasta != null && del != null && al != null) {
         return dao.findDesdeHasta(desde, hasta, del, al);
      } else {
         return dao.findAll();
      }
   }

   // Busca por Cliente (simpres dentro de fechas)
   public List<Facturacion> findByCliente(String cliente, Date del, Date al) {
      if (cliente == null || del == null || al == null) {
         return dao.findAll();
      }
      return dao.findByCliente(cliente, del, al);
   }

   // Busca la última Facturación
   public Facturacion ultimo() {
      return dao.findTopByOrderByIdfacturacionDesc().stream().findFirst().orElse(null);
   }

   public Optional<Facturacion> findById(Long id) {
      return dao.findById(id);
   }

   public List<FacturacionCuotasPendientesDTO> findFacturacionesConCuotasPendientes(
         Long desde,
         Long hasta,
         Date del,
         Date al,
         String cliente) {
      String clienteFiltro = cliente == null ? "" : cliente.trim().toLowerCase();

      List<Liquidafac> liquidaciones = liquidafacR.findPendientesFacturacionConDetalle();

      Map<Long, FacturacionCuotasPendientesDTO> resumen = new LinkedHashMap<>();

      for (Liquidafac liquidacion : liquidaciones) {
         if (liquidacion == null || liquidacion.getIdfacturacion_facturacion() == null) {
            continue;
         }

         Facturacion facturacion = liquidacion.getIdfacturacion_facturacion();
         Long idfacturacion = facturacion.getIdfacturacion();
         if (idfacturacion == null) {
            continue;
         }

         if (desde != null && idfacturacion < desde) {
            continue;
         }

         if (hasta != null && idfacturacion > hasta) {
            continue;
         }

         Date fechaFacturacion = facturacion.getFeccrea();
         if (del != null && (fechaFacturacion == null || fechaFacturacion.before(del))) {
            continue;
         }

         if (al != null && (fechaFacturacion == null || fechaFacturacion.after(al))) {
            continue;
         }

         String nombreCliente = facturacion.getIdcliente_clientes() != null
               ? facturacion.getIdcliente_clientes().getNombre()
               : "";

         if (!clienteFiltro.isEmpty() && (nombreCliente == null || !nombreCliente.toLowerCase().contains(clienteFiltro))) {
            continue;
         }

         FacturacionCuotasPendientesDTO item = resumen.computeIfAbsent(idfacturacion, key -> {
            FacturacionCuotasPendientesDTO dto = new FacturacionCuotasPendientesDTO();
            dto.setIdfacturacion(facturacion.getIdfacturacion());
            dto.setFeccrea(facturacion.getFeccrea());
            dto.setCliente(facturacion.getIdcliente_clientes() != null ? facturacion.getIdcliente_clientes().getNombre() : null);
            dto.setDescripcion(facturacion.getDescripcion());
            dto.setValorFacturacion(facturacion.getTotal() != null ? BigDecimal.valueOf(facturacion.getTotal()) : BigDecimal.ZERO);
            dto.setCuotas(facturacion.getCuotas() != null ? Long.valueOf(facturacion.getCuotas()) : 0L);
            dto.setPlanillasGeneradas(0L);
            dto.setPlanillasPagadas(0L);
            dto.setPlanillasPendientes(0L);
            dto.setValorPlanillas(BigDecimal.ZERO);
            dto.setValorPendiente(BigDecimal.ZERO);
            return dto;
         });

         item.setPlanillasGeneradas(item.getPlanillasGeneradas() + 1);

         BigDecimal valorPlanilla = liquidacion.getValor() != null
               ? BigDecimal.valueOf(liquidacion.getValor())
               : liquidacion.getIdfactura_facturas() != null && liquidacion.getIdfactura_facturas().getTotaltarifa() != null
                     ? liquidacion.getIdfactura_facturas().getTotaltarifa()
                     : BigDecimal.ZERO;

         item.setValorPlanillas(item.getValorPlanillas().add(valorPlanilla));

         Integer pagado = liquidacion.getIdfactura_facturas() != null
               ? liquidacion.getIdfactura_facturas().getPagado()
               : null;

         if (pagado != null && pagado == 1) {
            item.setPlanillasPagadas(item.getPlanillasPagadas() + 1);
         } else {
            item.setPlanillasPendientes(item.getPlanillasPendientes() + 1);
            item.setValorPendiente(item.getValorPendiente().add(valorPlanilla));
         }
      }

      List<FacturacionCuotasPendientesDTO> respuesta = new ArrayList<>();
      for (FacturacionCuotasPendientesDTO item : resumen.values()) {
         if (item.getPlanillasPendientes() != null && item.getPlanillasPendientes() > 0) {
            respuesta.add(item);
         }
      }
      return respuesta;
   }

   public <S extends Facturacion> S save(S entity) {
      return dao.save(entity);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}
