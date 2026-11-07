package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.epmapat.erp_epmapat.DTO.contabilidad.SinafipResultado;
import com.epmapat.erp_epmapat.DTO.contabilidad.SinafipAsignacion;
import com.epmapat.erp_epmapat.DTO.contabilidad.SinafipResultado.Bloque;
import com.epmapat.erp_epmapat.repositorio.contabilidad.SinafipConsulta;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = org.springframework.transaction.annotation.Isolation.REPEATABLE_READ)
public class SinafipServicio {
   private final SinafipConsulta consulta;

   public List<Map<String,Object>> asignaciones(int anio, int periodo) {
      validarEjercicio(anio,periodo);
      return consulta.reciprocas(LocalDate.of(anio,periodo,periodo==1 ? 2 : 1),YearMonth.of(anio,periodo).atEndOfMonth());
   }

   public List<Map<String,Object>> cuentasSinafip() { return consulta.cuentasSinafip(); }

   @Transactional(readOnly = false)
   public void asignar(SinafipAsignacion datos) {
      if (datos==null || datos.origen()==null || datos.idregistro()<=0 || datos.codcuereci()==null
            || !datos.codcuereci().matches("[0-9]{3}\\.[0-9]{2}(\\.[0-9]{2})?"))
         throw new IllegalArgumentException("Seleccione una cuenta reciproca valida");
      String partida=Objects.toString(datos.codparreci(),"").trim();
      if (!partida.isEmpty() && !partida.matches("[0-9]{2}\\.[0-9]{2}\\.[0-9]{2}"))
         throw new IllegalArgumentException("La partida reciproca debe tener formato 00.00.00 o quedar vacia");
      if (datos.asierefe()!=null && datos.asierefe()<0) throw new IllegalArgumentException("Asiento de referencia no valido");
      consulta.asignar(new SinafipAsignacion(datos.origen(),datos.idregistro(),datos.codcuereci(),partida.isEmpty()?null:partida,datos.asierefe()));
   }

   private void validarEjercicio(int anio,int periodo) {
      if (anio < 2000 || anio > 2100 || periodo < 1 || periodo > 12)
         throw new IllegalArgumentException("Anio o periodo fuera de rango");
      Integer ejercicio = consulta.ejercicio();
      if (ejercicio != null && ejercicio != anio)
         throw new IllegalArgumentException("La base de datos corresponde al ejercicio " + ejercicio
               + ". Seleccione ese ejercicio para no mezclar movimientos con otro plan de cuentas o presupuesto.");
   }

   public SinafipResultado generar(String archivo, int anio, int periodo) {
      validarEjercicio(anio,periodo);
      Integer ejercicio = consulta.ejercicio();
      LocalDate desde = LocalDate.of(anio, 1, 1);
      boolean inicial = Set.of("apertura", "detalle-apertura", "presupuesto").contains(archivo);
      LocalDate hasta = inicial ? LocalDate.of(anio, 12, 31) : YearMonth.of(anio, periodo).atEndOfMonth();
      List<String> avisos = new ArrayList<>();
      if (ejercicio == null) avisos.add("No se pudo identificar el ejercicio en definir.fechap; verifique que la base corresponda al anio seleccionado.");
      List<Bloque> bloques = switch (archivo) {
         case "apertura" -> List.of(apertura(consulta.cuentas(desde, hasta)));
         case "balance" -> List.of(balance(consulta.cuentas(desde, hasta), periodo));
         case "detalle-apertura" -> List.of(detalle(consulta.detalleApertura(desde, hasta)));
         case "presupuesto", "cedula" -> presupuesto(consulta.presupuesto(desde, hasta),
               consulta.funcionesEspeciales(), archivo.equals("cedula"), periodo, avisos);
         case "reciprocas" -> List.of(reciprocas(consulta.reciprocas(
               LocalDate.of(anio, periodo, periodo == 1 ? 2 : 1), hasta), consulta.rucInstitucion(), periodo));
         default -> throw new IllegalArgumentException("Archivo SINAFIP no reconocido");
      };
      if (bloques.stream().allMatch(b -> b.filas().isEmpty())) avisos.add("No hay registros para los filtros seleccionados.");
      return new SinafipResultado(archivo, anio, inicial ? 1 : periodo, hasta.toString(), bloques, avisos);
   }

   public Bloque apertura(List<Map<String, Object>> cuentas) {
      List<List<String>> filas = new ArrayList<>();
      for (var cuenta : cuentas) {
         BigDecimal neto = numero(cuenta, "apertura_debe").subtract(numero(cuenta, "apertura_haber"));
         BigDecimal debe = neto.max(BigDecimal.ZERO), haber = neto.negate().max(BigDecimal.ZERO);
         if (debe.signum() == 0 && haber.signum() == 0) continue;
         List<String> fila = fila("01", cuenta(cuenta));
         fila.addAll(List.of(importe(debe), importe(haber)));
         filas.add(fila);
      }
      return new Bloque("Apertura inicial", List.of("Periodo", "Cuenta", "Subcuenta", "Auxiliar", "Debe inicial", "Haber inicial"), filas);
   }

   public Bloque balance(List<Map<String, Object>> cuentas, int periodo) {
      List<List<String>> filas = new ArrayList<>();
      for (var cuenta : cuentas) {
         BigDecimal neto = numero(cuenta, "apertura_debe").subtract(numero(cuenta, "apertura_haber"));
         BigDecimal inicialDebe = neto.max(BigDecimal.ZERO), inicialHaber = neto.negate().max(BigDecimal.ZERO);
         BigDecimal debe = numero(cuenta, "movimiento_debe"), haber = numero(cuenta, "movimiento_haber");
         if (inicialDebe.signum() == 0 && inicialHaber.signum() == 0 && debe.signum() == 0 && haber.signum() == 0) continue;
         BigDecimal acumuladoDebe = inicialDebe.add(debe), acumuladoHaber = inicialHaber.add(haber);
         BigDecimal saldo = acumuladoDebe.subtract(acumuladoHaber);
         List<String> fila = fila(mes(periodo), cuenta(cuenta));
         fila.addAll(List.of(importe(inicialDebe), importe(inicialHaber), importe(debe), importe(haber),
               importe(acumuladoDebe), importe(acumuladoHaber), importe(saldo.max(BigDecimal.ZERO)), importe(saldo.negate().max(BigDecimal.ZERO))));
         filas.add(fila);
      }
      return new Bloque("Balance de comprobacion", List.of("Periodo", "Cuenta", "Subcuenta", "Auxiliar",
            "Debe inicial", "Haber inicial", "Debe movimiento", "Haber movimiento", "Debe acumulado", "Haber acumulado", "Saldo deudor", "Saldo acreedor"), filas);
   }

   public Bloque detalle(List<Map<String, Object>> datos) {
      Map<String, Detalle> grupos = new TreeMap<>();
      for (var dato : datos) {
         String codigo = String.join(".", cuenta(dato));
         boolean consumidorFinal = numero(dato, "swconsufin").intValue() == 1;
         String ruc = consumidorFinal ? "9999999999999" : texto(dato, "rucben");
         if (ruc.length() != 13 && ruc.length() != 10) ruc = texto(dato, "ciben");
         if (ruc.isBlank()) throw new IllegalArgumentException("Beneficiario sin identificacion en la cuenta " + codigo);
         String key = codigo + "|" + ruc;
         Detalle grupo = grupos.get(key);
         if (grupo == null) { grupo = new Detalle(codigo, ruc, consumidorFinal ? "Consumidor Final" : limpiar(Objects.toString(dato.get("nomben"), ""))); grupos.put(key, grupo); }
         // El sistema anterior toma el valor distribuido en benextran, no el total de transaci.
         if (numero(dato, "tiptran").intValue() == 4) grupo.debe = grupo.debe.add(numero(dato, "valor"));
         else if (numero(dato, "tiptran").intValue() == 7) grupo.haber = grupo.haber.add(numero(dato, "valor"));
         else throw new IllegalArgumentException("Movimiento sin Debe/Haber en la cuenta " + codigo);
      }
      List<List<String>> filas = new ArrayList<>();
      for (var grupo : grupos.values()) {
         List<String> fila = fila("01", grupo.codigo.split("\\."));
         fila.addAll(List.of(grupo.ruc, grupo.nombre, importe(grupo.debe), importe(grupo.haber)));
         filas.add(fila);
      }
      return new Bloque("Detalle apertura inicial", List.of("Periodo", "Cuenta", "Subcuenta", "Auxiliar", "Identificacion", "Beneficiario", "Debe", "Haber"), filas);
   }

   public List<Bloque> presupuesto(List<Map<String, Object>> datos, List<Map<String, Object>> especiales,
         boolean cedula, int periodo, List<String> avisos) {
      Map<String, Presupuesto> grupos = new TreeMap<>();
      for (var dato : datos) {
         int tipo = numero(dato, "tippar").intValue();
         if (tipo != 1 && tipo != 2) throw new IllegalArgumentException("Tipo de partida no valido");
         if (!cedula && numero(dato, "inicia").signum() <= 0) continue;
         String codpar = texto(dato, "codpar");
         Map<String,Object> especial = tipo == 2 ? especiales.stream()
               .filter(e -> !texto(e,"codigo").isBlank() && codpar.startsWith(texto(e,"codigo") + "."))
               .findFirst().orElse(null) : null;
         String codigo = tipo == 1 ? codpar : especial != null ? codpar.substring(codpar.indexOf('.')+1) : texto(dato, "codigo");
         String[] partes = codigo.split("\\.");
         if (partes.length < 3 || !String.join("", Arrays.copyOf(partes,3)).matches("[0-9]+"))
            throw new IllegalArgumentException("Partida sin codigo presupuestario valido: " + codpar);
         codigo = String.join(".", Arrays.copyOf(partes,3));
         // El legado conserva cada actividad (primeros 15 caracteres) de la funcion D.
         String funcion = especial == null ? "000" : texto(especial,"funcion");
         String key = tipo + "|" + (especial == null ? "0|"+codigo : "1|"+codpar.substring(0,Math.min(15,codpar.length())));
         Presupuesto grupo = grupos.get(key);
         if (grupo == null) {
            grupo = new Presupuesto(tipo, partes, funcion, especial != null);
            grupos.put(key,grupo);
         }
         grupo.inicial = grupo.inicial.add(numero(dato, "inicia"));
         grupo.reformas = grupo.reformas.add(numero(dato, "modifi"));
         grupo.comprometido = grupo.comprometido.add(numero(dato, "prmiso"));
         grupo.devengado = grupo.devengado.add(numero(dato, "devengado"));
         grupo.pagado = grupo.pagado.add(numero(dato, "cobpagado"));
      }
      List<List<String>> ingresos = new ArrayList<>(), gastos = new ArrayList<>();
      for (var grupo : grupos.values()) {
         BigDecimal codificado = grupo.inicial.add(grupo.reformas);
         if (cedula && grupo.inicial.signum()==0 && codificado.signum()==0 && grupo.comprometido.signum()==0
               && grupo.devengado.signum()==0 && grupo.pagado.signum()==0) continue;
         List<String> fila = new ArrayList<>(List.of(cedula ? mes(periodo) : grupo.especial ? "1" : "01", grupo.tipo == 1 ? "I" : "G"));
         fila.addAll(Arrays.asList(Arrays.copyOf(grupo.partes,3)));
         if (grupo.tipo == 2) fila.addAll(List.of(grupo.funcion, "99999999"));
         fila.add(importe(grupo.inicial));
         if (cedula) {
            fila.addAll(List.of(importe(grupo.reformas), importe(codificado)));
            if (grupo.tipo == 2) fila.add(importe(grupo.comprometido));
            fila.addAll(List.of(importe(grupo.devengado), importe(grupo.pagado)));
            if (grupo.tipo == 2) fila.add(importe(codificado.subtract(grupo.comprometido)));
            fila.add(importe(codificado.subtract(grupo.devengado)));
         }
         (grupo.tipo == 1 ? ingresos : gastos).add(fila);
      }
      return List.of(new Bloque("Ingresos", columnasPresupuesto(false, cedula), ingresos),
            new Bloque("Gastos", columnasPresupuesto(true, cedula), gastos));
   }

   private List<String> columnasPresupuesto(boolean gasto, boolean cedula) {
      List<String> columnas = new ArrayList<>(List.of("Periodo", "Tipo", "Grupo", "Subgrupo", "Item"));
      if (gasto) columnas.addAll(List.of("Funcion", "Proyecto"));
      columnas.add("Inicial");
      if (cedula) {
         columnas.addAll(List.of("Reformas", "Codificado"));
         if (gasto) columnas.add("Comprometido");
         columnas.addAll(List.of("Devengado", gasto ? "Pagado" : "Cobrado"));
         if (gasto) columnas.add("Saldo por comprometer");
         columnas.add("Saldo por devengar");
      }
      return columnas;
   }

   public Bloque reciprocas(List<Map<String,Object>> datos, String rucInstitucion, int periodo) {
      if (rucInstitucion == null || !rucInstitucion.trim().matches("[0-9]{13}"))
         throw new IllegalArgumentException("Registre el RUC de 13 digitos de la institucion en Definir.");
      Map<String,Reciproca> grupos = new TreeMap<>();
      for (var dato : datos) {
         boolean consumidor = numero(dato,"swconsufin").intValue()!=0;
         String ruc = consumidor ? "9999999999999" : texto(dato,"rucben");
         if (ruc.length()<10) ruc=texto(dato,"ciben");
         if (ruc.isBlank()) throw new IllegalArgumentException("Beneficiario sin identificacion en las transacciones reciprocas.");
         int tipo = numero(dato,"tiptran").intValue();
         String codigo = texto(dato,"cuenta_sinafip");
         String asiento = texto(dato,"asiento");
         String key = mes(tipo)+" "+codigo+" "+asiento+" "+ruc;
         Reciproca grupo = grupos.get(key);
         if (grupo == null) { grupo=new Reciproca(); grupos.put(key,grupo); }
         grupo.ultimo=dato;
         grupo.ruc=ruc;
         grupo.nombre=numero(dato,"swconsufin").intValue()==1 ? "Consumidor Final" : limpiar(Objects.toString(dato.get("nomben"),""));
         grupo.cantidad++;
         if (numero(dato,"debcre").intValue()==1) grupo.debe=grupo.debe.add(numero(dato,"valor"));
         else if (numero(dato,"debcre").intValue()==2) grupo.haber=grupo.haber.add(numero(dato,"valor"));
         else throw new IllegalArgumentException("Transaccion reciproca sin indicador Debe/Haber.");
      }
      List<List<String>> filas = new ArrayList<>();
      for (Reciproca grupo : grupos.values()) {
         var dato=grupo.ultimo;
         String codigo=texto(dato,"cuenta_sinafip"), contra=texto(dato,"codcuereci"), codpar=texto(dato,"codparreci");
         if (contra.isBlank()) throw new IllegalArgumentException("Asigne la cuenta reciproca de la transaccion "
               +texto(dato,"inttra")+" antes de exportar.");
         String[] cuenta=cuenta(Map.of("codcue",codigo));
         String[] cuentaIog=cuenta(Map.of("codcue",contra));
         List<String> fila = new ArrayList<>(List.of(mes(periodo),rucInstitucion.trim()));
         fila.addAll(Arrays.asList(cuenta));
         fila.addAll(List.of(importe(grupo.debe),importe(grupo.haber),grupo.ruc,grupo.nombre));
         if (codpar.isBlank()) fila.addAll(List.of("","",""));
         else {
            String[] partes=codpar.split("\\.");
            if (partes.length<3) throw new IllegalArgumentException("Partida reciproca incompleta: "+codpar);
            fila.addAll(Arrays.asList(Arrays.copyOf(partes,3)));
         }
         fila.addAll(Arrays.asList(cuentaIog));
         // La contrapartida invierte Debe/Haber, igual que wf_reciprocas.
         fila.addAll(List.of(importe(grupo.haber),importe(grupo.debe),texto(dato,"asiento")));
         int tipo=numero(dato,"tiptran").intValue();
         String referencia=tipo<=6 ? texto(dato,"asiento") : texto(dato,"asierefe");
         if ((grupo.cantidad>1 && tipo==9) || tipo==12) referencia="0";
         if (referencia.isBlank()) throw new IllegalArgumentException("Falta el asiento de referencia de la transaccion "+texto(dato,"inttra"));
         Object fecha=dato.get("fecha");
         LocalDate dia=fecha instanceof java.sql.Timestamp timestamp ? timestamp.toLocalDateTime().toLocalDate()
               : fecha instanceof java.sql.Date sqlDate ? sqlDate.toLocalDate() : LocalDate.parse(fecha.toString());
         String fechaArchivo=dia.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu"));
         fila.addAll(List.of(referencia,fechaArchivo,fechaArchivo));
         filas.add(fila);
      }
      return new Bloque("Transacciones reciprocas",List.of("Periodo","RUC institucion","Cuenta","Subcuenta","Auxiliar",
            "Debe","Haber","Identificacion","Beneficiario","Grupo partida","Subgrupo partida","Item partida",
            "Cuenta contraparte","Subcuenta contraparte","Auxiliar contraparte","Debe contraparte","Haber contraparte",
            "Asiento","Asiento referencia","Fecha","Vencimiento"),filas);
   }

   public static String importe(BigDecimal valor) {
      return valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
   }

   private String[] cuenta(Map<String, Object> dato) {
      String codigo = texto(dato, "codcue");
      String[] partes = codigo.split("\\.");
      if (partes.length < 2 || !String.join("", partes).matches("[0-9]+"))
         throw new IllegalArgumentException("Cuenta sin estructura SINAFIP valida: " + codigo);
      return new String[]{partes[0], partes[1], partes.length > 2 ? partes[2] : "00"};
   }
   private List<String> fila(String periodo, String[] codigo) {
      List<String> fila = new ArrayList<>(); fila.add(periodo); fila.addAll(Arrays.asList(codigo)); return fila;
   }
   private String mes(int periodo) { return String.format(Locale.ROOT, "%02d", periodo); }
   private String texto(Map<String, Object> dato, String key) { return Objects.toString(dato.get(key), "").trim(); }
   private String limpiar(String texto) { return texto.replace('|', ' ').replaceAll("[\\r\\n\\t]+", " "); }
   private BigDecimal numero(Map<String, Object> dato, String key) {
      Object value = dato.get(key); return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
   }
   private static class Detalle {
      final String codigo, ruc, nombre;
      BigDecimal debe = BigDecimal.ZERO, haber = BigDecimal.ZERO;
      Detalle(String codigo, String ruc, String nombre) { this.codigo = codigo; this.ruc = ruc; this.nombre = nombre; }
   }
   private static class Presupuesto {
      final int tipo; final String[] partes; final String funcion; final boolean especial;
      BigDecimal inicial = BigDecimal.ZERO, reformas = BigDecimal.ZERO, comprometido = BigDecimal.ZERO,
            devengado = BigDecimal.ZERO, pagado = BigDecimal.ZERO;
      Presupuesto(int tipo, String[] partes, String funcion, boolean especial) {
         this.tipo = tipo; this.partes = partes; this.funcion = funcion; this.especial = especial;
      }
   }
   private static class Reciproca {
      Map<String,Object> ultimo;
      String ruc,nombre;
      int cantidad;
      BigDecimal debe=BigDecimal.ZERO,haber=BigDecimal.ZERO;
   }
}
