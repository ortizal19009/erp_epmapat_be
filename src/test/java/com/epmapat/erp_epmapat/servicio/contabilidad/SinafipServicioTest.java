package com.epmapat.erp_epmapat.servicio.contabilidad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.repositorio.contabilidad.SinafipConsulta;

class SinafipServicioTest {
   private final SinafipServicio service = new SinafipServicio(mock(SinafipConsulta.class));

   @Test void balanceCoincideConUnaFilaDeLaReferenciaPowerBuilder() {
      var dato = Map.<String,Object>of("codcue", "111.01", "apertura_debe", "383.02", "apertura_haber", "0",
            "movimiento_debe", "260904.70", "movimiento_haber", "249291.49");
      assertEquals("01|111|01|00|383,02|0,00|260904,70|249291,49|261287,72|249291,49|11996,23|0,00",
            String.join("|", service.balance(List.of(dato),1).filas().get(0)));
   }

   @Test void aperturaConservaAuxiliarYOmiteCuentasSinApertura() {
      var valor = Map.<String,Object>of("codcue", "112.01.02", "apertura_debe", "12220.86");
      var cero = Map.<String,Object>of("codcue", "111.02");
      assertEquals(List.of(List.of("01","112","01","02","12220,86","0,00")), service.apertura(List.of(valor,cero)).filas());
   }

   @Test void detalleAgrupaDistribucionesYUsaTipoDeTransaccionDelLegado() {
      var dato = new HashMap<String,Object>(Map.of("codcue","124.97.07.001", "rucben","1234567890",
            "nomben","Beneficiario | prueba\nsegunda linea", "tiptran",4, "debcre",2, "valor","100.01"));
      var otro = new HashMap<>(dato); otro.put("valor","20.02");
      var filas = service.detalle(List.of(dato,otro)).filas();
      assertEquals(1, filas.size());
      assertEquals("120,03",filas.get(0).get(6));
      assertEquals("1234567890",filas.get(0).get(4));
      assertFalse(filas.get(0).get(5).contains("|"));
      assertFalse(filas.get(0).get(5).contains("\n"));
   }

   @Test void presupuestoNoMezclaIngresoYGastoDelMismoCodigo() {
      var ingreso = Map.<String,Object>of("tippar",1,"codpar","51.01.05.001","inicia","100.01");
      var gasto = Map.<String,Object>of("tippar",2,"codpar","01.51.01.05.001","codigo","51.01.05","inicia","200.02");
      var bloques = service.presupuesto(List.of(ingreso,gasto),List.of(),false,1,new ArrayList<>());
      assertEquals("100,01", bloques.get(0).filas().get(0).get(5));
      assertEquals("200,02", bloques.get(1).filas().get(0).get(7));
      assertEquals(6, bloques.get(0).filas().get(0).size());
      assertEquals(8, bloques.get(1).filas().get(0).size());
   }

   @Test void cedulaIncluyeInicialSinMovimientosYCalculaSaldos() {
      var gasto = Map.<String,Object>of("tippar",2,"codpar","01.51.01.05.001","codigo","51.01.05",
            "inicia","1000","modifi","-100","prmiso","300","devengado","200","cobpagado","150");
      var bloques = service.presupuesto(List.of(gasto),List.of(),true,12,new ArrayList<>());
      var fila = bloques.get(1).filas().get(0);
      assertEquals(15, fila.size()); assertEquals("12",fila.get(0));
      assertEquals("900,00",fila.get(9)); assertEquals("600,00",fila.get(13)); assertEquals("700,00",fila.get(14));
      assertEquals(bloques.get(1).columnas().size(),fila.size());
   }

   @Test void rechazaEjercicioDiferenteYConservaFormatoDeFuncionesEspeciales() {
      SinafipConsulta consulta = mock(SinafipConsulta.class);
      when(consulta.ejercicio()).thenReturn(2026);
      assertThrows(IllegalArgumentException.class, () -> new SinafipServicio(consulta).generar("balance",2025,1));
      var gasto = Map.<String,Object>of("tippar",2,"codpar","99.51.01.05.001","codigo","51.01.05","inicia",100);
      var especial = service.presupuesto(List.of(gasto),List.of(Map.of("codigo","99","funcion","D01")),false,1,new ArrayList<>());
      assertEquals(List.of("1","G","51","01","05","D01","99999999","100,00"),especial.get(1).filas().get(0));
      assertEquals("0,00", SinafipServicio.importe(new BigDecimal("-0.001")));
      assertEquals("1,01", SinafipServicio.importe(new BigDecimal("1.005")));
   }

   @Test void aperturaNeteaAmbosLadosIgualQueWfAperturainicial() {
      var cuenta = Map.<String,Object>of("codcue","111.01","apertura_debe","100.01","apertura_haber","25.01");
      assertEquals(List.of("01","111","01","00","75,00","0,00"),service.apertura(List.of(cuenta)).filas().get(0));
   }

   @Test void reciprocasTiene21CamposInvierteContrapartidaYAgrupaPagosSinDuplicar() {
      Map<String,Object> dato = new HashMap<>();
      dato.put("tiptran",9); dato.put("codcue","113.01.01.001"); dato.put("cuenta_sinafip","113.01.01");
      dato.put("rucben","1234567890001"); dato.put("nomben","Proveedor"); dato.put("debcre",2); dato.put("valor","10.01");
      dato.put("asiento",25); dato.put("asierefe",7); dato.put("fecha",java.sql.Timestamp.valueOf("2026-01-20 15:45:00"));
      dato.put("codcuereci","213.01"); dato.put("inttra",11);
      var resultado = service.reciprocas(List.of(dato,dato),"1234567890001",1);
      var fila=resultado.filas().get(0);
      assertEquals(1,resultado.filas().size()); assertEquals(21,fila.size());
      assertEquals(resultado.columnas().size(),fila.size());
      assertEquals("20,02",fila.get(6)); assertEquals("20,02",fila.get(15));
      assertEquals("0",fila.get(18)); assertEquals("20/01/2026",fila.get(19));
      assertEquals(List.of("","",""),fila.subList(9,12));
      dato.put("codcuereci","");
      assertThrows(IllegalArgumentException.class,()->service.reciprocas(List.of(dato),"1234567890001",1));
   }
}
