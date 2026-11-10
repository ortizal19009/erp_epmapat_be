# Resumen facturación

Disponible en Imprimir Recaudación General / Cajas (`imp-cajas`), opción **Resumen facturación**, con fechas Desde/Hasta y formatos PDF, XML, XLSX y CSV.

Plantilla: src/main/resources/reports/ConteoFacturacionPorFormaPago.jrxml.
Conserva el nombre tecnico para no romper llamadas existentes.

Solo recibe desde / hasta (java.sql.Date) e idusuario (java.lang.Integer).
El endpoint general interpreta fechas de entrada como Timestamp. BuildReports
las normaliza a la clase declarada en la plantilla, incluida java.sql.Date,
antes de evaluar expresiones o ejecutar el reporte. Esto evita el
ClassCastException Timestamp -> Date observado al mostrar el periodo.
El usuario se obtiene de usuarios.nomusu. Logo y marca de agua incrustados.
El pie incluye una tabla con usuario y fecha/hora de impresion (dd/MM/yyyy
HH:mm:ss, zona America/Guayaquil), con el mismo instante en todas las paginas.
Actualizar la copia externa y recompilar cualquier .jasper que use el servidor.

## Fechas y corte

- Cobros: facturas.fechacobro dentro del rango, incluyendo ambos dias.
- No efectivizadas: forma 4, fechatransferencia dentro del rango y estado 3.
- Historico: una transferencia con estado actual 1 y fechacobro posterior a
  hasta sigue como NO EFECTIVIZADA en el periodo de su fechatransferencia.
  Si se efectiviza en octubre, conserva su fila pendiente de septiembre y
  aparece como EFECTIVIZADA en octubre por fechacobro. Son movimientos de
  control de cada periodo; no sumar ambos meses como facturas unicas.
- Estado 3 sin fechatransferencia no entra al reporte: no se reemplaza esa
  fecha por fechacobro, que puede cambiar.
- No se agrega un parametro adicional para el corte: se usa hasta.

El cambio de estado 3 a 1 y de fechacobro no altera el conteo historico de
pendientes. Esto no es una fotografia contable inmutable: los importes se
calculan con los rubros, IVA e intereses guardados actualmente. Si tambien
se modifican esos importes, las fechas originales, la forma de pago o se
eliminan registros fisicamente, no existe aqui una copia historica para
recuperar el valor anterior. Para esa garantia se necesita persistir cierres
mensuales o un historial de movimientos e importes.

## Cantidades e importes

Cantidad es COUNT(*) de facturas, nunca suma de valores. Los rubros se
agrupan previamente por idfactura para no multiplicar el conteo.
Valor total suma rubros activos (cantidad por valor unitario), con el
redondeo por rubro usado por FacturasCobradas, mas interescobrado y swiva.
Excluye rubros 5 y 165 del subtotal, y aplica swmulta / swcondonar.
Los pendientes no representan dinero efectivamente recaudado.

## Estadisticas independientes

Anuladas: fecha de anulacion dentro del rango.
Eliminadas: fecha de eliminacion dentro del rango.
Refacturadas: facturas originales eliminadas en el rango con lectura anterior
vinculada a una lectura nueva y otra factura por emisionindividual.
Cuenta una sola vez cada original aunque existan multiples relaciones.
Refacturadas es subconjunto de eliminadas. Las estadisticas no se suman
al total de cantidades ni importes, y aparecen aun sin cobros en el periodo.

## Invocacion

POST /jasperReports/reportes

```json
{
  "reportName": "ConteoFacturacionPorFormaPago",
  "extencion": "pdf",
  "parameters": {
    "desde": "2026-09-01",
    "hasta": "2026-09-30",
    "idusuario": 1
  }
}
```

Para el catalogo Reportes JR: metodo 1 (SQL) y solo esos tres parametros.
Actualizar tambien la plantilla en REPORTESJR_DIRECTORY si usa copia externa.

## Validacion

powershell -ExecutionPolicy Bypass -File scripts/test-conteo-facturacion.ps1

Crea y detiene PostgreSQL temporal local sin leer configuracion productiva.
Prueba 20000 facturas / 60000 rubros, transferencia de un mes efectivizada
al siguiente, pendientes con fecha de cobro distinta, ausencia de duplicados,
fechas de estadisticas, refacturaciones multiples y periodo sin cobros.
Tambien compila Jasper y verifica totales, estadisticas, logo y fondo en PDF.
Vista previa ficticia: target/conteo-preview/conteo.pdf y conteo.png.
Requiere PostgreSQL 12 o posterior (CTE MATERIALIZED).
