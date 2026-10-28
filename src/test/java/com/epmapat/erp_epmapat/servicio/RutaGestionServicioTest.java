package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import javax.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;
import com.epmapat.erp_epmapat.DTO.RutaGuardarRequest;
import com.epmapat.erp_epmapat.DTO.RutaGuardarRequest.CuentaTraslado;
class RutaGestionServicioTest {
    RutasR rutas=mock(RutasR.class);AbonadosR abonados=mock(AbonadosR.class);JdbcTemplate jdbc=mock(JdbcTemplate.class);
    EntityManager em=mock(EntityManager.class);AuditoriaGenericaService auditoria=mock(AuditoriaGenericaService.class);
    RutaHistorialServicio historial=new RutaHistorialServicio(auditoria);
    RutaGestionServicio servicio=new RutaGestionServicio(rutas,abonados,historial,jdbc);
    RutaGestionServicioTest(){
      ReflectionTestUtils.setField(servicio,"em",em);
      when(jdbc.queryForObject(startsWith("SELECT COUNT"),eq(Integer.class),any(),any(),any())).thenReturn(0);
      when(jdbc.queryForObject(startsWith("SELECT COALESCE"),eq(Long.class),any())).thenReturn(10L);
      when(jdbc.queryForList("SELECT codigo FROM rutas",String.class)).thenReturn(List.of("20400170","20400172"));
      when(jdbc.queryForObject("SELECT COALESCE(MAX(orden),0) FROM rutas",Long.class)).thenReturn(114L);
      when(rutas.saveAndFlush(any(Rutas.class))).thenAnswer(i->{Rutas r=i.getArgument(0);if(r.getIdruta()==null)r.setIdruta(10L);return r;});
    }
    Rutas ruta(long id){Rutas r=new Rutas();r.setIdruta(id);r.setCodigo("R"+id);r.setDescripcion("Ruta "+id);r.setEstado(true);return r;}
    Abonados cuenta(long id,Rutas ruta){Abonados a=new Abonados();a.setIdabonado(id);a.setIdruta_rutas(ruta);a.setSecuencia(2L);a.setNromedidor("MED");a.setEstado(2L);return a;}
    RutaGuardarRequest solicitud(List<CuentaTraslado> cuentas){return new RutaGuardarRequest("DEST","Destino",1L,true,7L,"Redistribucion",cuentas);}
    @Test void nuevaRutaRecibeCuentasDeVariosOrigenesConHistorialInmutable() {
      var uno=cuenta(1,ruta(20));var dos=cuenta(2,ruta(30));
      when(abonados.lockParaRetiro(List.of(1L,2L))).thenReturn(List.of(uno,dos));
      var creada=servicio.guardar(null,solicitud(List.of(new CuentaTraslado(2L,30L),new CuentaTraslado(1L,20L))),7L);
      assertEquals("20400173",creada.getCodigo());assertEquals(115L,creada.getOrden());assertEquals(10L,creada.getIdruta());assertEquals(7L,creada.getUsucrea());assertNotNull(creada.getFeccrea());
      assertSame(creada,uno.getIdruta_rutas());assertEquals(11L,uno.getSecuencia());assertEquals(12L,dos.getSecuencia());
      assertEquals("MED",uno.getNromedidor());assertEquals(2L,uno.getEstado());
      var cambios=org.mockito.ArgumentCaptor.forClass(RutaHistorialServicio.Cambio.class);
      verify(auditoria,times(2)).saveAuditAtomic(eq("abonados"),anyLong(),cambios.capture(),eq(7L),eq("Redistribucion"),eq("CAMBIO_RUTA"));
      assertEquals(20L,cambios.getAllValues().get(0).rutaAnterior().id());assertEquals(10L,cambios.getValue().rutaNueva().id());
      assertEquals("RUTAS",cambios.getValue().origen());verify(abonados).save(uno);verify(abonados).save(dos);
    }
    @Test void editarConservaCreacionYActualizaEstadoSinCrearOtraRuta() {
      Rutas destino=ruta(10);destino.setUsucrea(4L);Date fecha=new Date(10000);destino.setFeccrea(fecha);
      when(em.find(Rutas.class,10L,LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
      var guardada=servicio.guardar(10L,new RutaGuardarRequest("EDIT","Editada",2L,false,7L,null,List.of()),7L);
      assertSame(destino,guardada);assertEquals(4L,guardada.getUsucrea());assertEquals(fecha,guardada.getFeccrea());
      assertEquals(7L,guardada.getUsumodi());assertFalse(guardada.getEstado());verifyNoInteractions(abonados,auditoria);
    }
    @Test void rechazaCambioConcurrenteSinGuardarCuentasNiHistorial() {
      var cuenta=cuenta(1,ruta(99));when(abonados.lockParaRetiro(List.of(1L))).thenReturn(List.of(cuenta));
      assertThrows(ResponseStatusException.class,()->servicio.guardar(null,solicitud(List.of(new CuentaTraslado(1L,20L))),7L));
      verify(abonados,never()).save(any());verifyNoInteractions(auditoria);assertEquals(99L,cuenta.getIdruta_rutas().getIdruta());
    }
    @Test void noCreaRutaSiElCodigoEstaDuplicado() {
      when(jdbc.queryForObject(startsWith("SELECT COUNT"),eq(Integer.class),any(),any(),any())).thenReturn(1);
      assertThrows(ResponseStatusException.class,()->servicio.guardar(null,solicitud(List.of()),7L));verify(rutas,never()).saveAndFlush(any());
    }
    @Test void exigeMotivoYRechazaCuentasDuplicadas() {
      var cuentas=List.of(new CuentaTraslado(1L,20L));
      assertThrows(ResponseStatusException.class,()->servicio.guardar(null,new RutaGuardarRequest("N","Nueva",0L,true,7L,"",cuentas),7L));
      assertThrows(ResponseStatusException.class,()->servicio.guardar(null,solicitud(List.of(cuentas.get(0),cuentas.get(0))),7L));verifyNoInteractions(rutas,abonados,auditoria);
    }
    @Test void unFalloDeAuditoriaAbortaElCambio() {
      var cuenta=cuenta(1,ruta(20));when(abonados.lockParaRetiro(List.of(1L))).thenReturn(List.of(cuenta));
      doThrow(new IllegalStateException("Auditoria no disponible")).when(auditoria).saveAuditAtomic(anyString(),anyLong(),any(),anyLong(),anyString(),anyString());
      assertThrows(IllegalStateException.class,()->servicio.guardar(null,solicitud(List.of(new CuentaTraslado(1L,20L))),7L));
      verify(abonados,never()).save(any());assertEquals(20L,cuenta.getIdruta_rutas().getIdruta());
    }
    @Test void modificarAbonadoUsaElMismoHistorialYNoDuplicaSiLaRutaNoCambio() {
      AbonadoServicio servicioAbonado=new AbonadoServicio();var original=cuenta(1,ruta(20));var cambios=cuenta(1,ruta(30));cambios.setSecuencia(8L);
      ReflectionTestUtils.setField(servicioAbonado,"dao",abonados);ReflectionTestUtils.setField(servicioAbonado,"entityManager",em);
      ReflectionTestUtils.setField(servicioAbonado,"rutaHistorial",historial);ReflectionTestUtils.setField(servicioAbonado,"auditoriaService",auditoria);
      when(abonados.findById(1L)).thenReturn(Optional.of(original));when(abonados.save(any(Abonados.class))).thenAnswer(i->i.getArgument(0));
      servicioAbonado.actualizarAbonadoConAuditoria(1L,cambios,7L,"Cambio desde cuenta","MODIFICACION");
      servicioAbonado.actualizarAbonadoConAuditoria(1L,cambios,7L,"Otro dato","MODIFICACION");
      var cambio=org.mockito.ArgumentCaptor.forClass(RutaHistorialServicio.Cambio.class);
      verify(auditoria).saveAuditAtomic(eq("abonados"),eq(1L),cambio.capture(),eq(7L),eq("Cambio desde cuenta"),eq("CAMBIO_RUTA"));
      assertEquals("ABONADOS",cambio.getValue().origen());assertEquals(20L,cambio.getValue().rutaAnterior().id());assertEquals(30L,cambio.getValue().rutaNueva().id());
    }
    @Test void codigosSecuencialesConservanCerosYNoReutilizanHuecos() {
      assertEquals("00000001",RutaGestionServicio.calcularSiguienteCodigo(List.of()));
      assertEquals("00000011",RutaGestionServicio.calcularSiguienteCodigo(List.of("00000002","00000010","OTRO")));
      assertThrows(ResponseStatusException.class,()->RutaGestionServicio.calcularSiguienteCodigo(List.of("99999999")));
    }
    @Test void editarConservaCodigoAunqueElFormularioEnvieOtro() {
      Rutas destino=ruta(10);when(em.find(Rutas.class,10L,LockModeType.PESSIMISTIC_WRITE)).thenReturn(destino);
      var resultado=servicio.guardar(10L,solicitud(List.of()),7L);
      assertEquals("R10",resultado.getCodigo());assertEquals(1L,resultado.getOrden());
    }

}
