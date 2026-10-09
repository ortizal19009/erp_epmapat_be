# ResumenFacturacion: control de facturación y recaudación

## Archivos y alcance

- Plantilla adaptada: `src/main/resources/reports/ResumenFacturacion.jrxml`. El archivo solicitado como `.xml` existe realmente con extensión `.jrxml`.
- Pruebas nuevas: `src/test/java/com/epmapat/erp_epmapat/jasperReports/ResumenFacturacionTest.java`.
- Ejecutor nuevo: `scripts/test-resumen-facturacion.ps1`.
- Este documento describe la integración, reglas y límites. La SQL completa se reproduce al final.
- La plantilla recibida era un comprobante de retención sin consulta. Se conservan sus parámetros institucionales, logo `/reports/LOGO-H.png`, fuente DejaVu Sans, formato A4 y márgenes de 20 puntos. El contenido de retención se sustituye por tablas de control, encabezados repetidos y numeración de páginas.
- No se cambian Java, Spring Boot, JasperReports **6.21.4**, dependencias, frontend ni otros módulos. No se modifica la base de producción.

## Integración existente reutilizada

| Componente | Implementación |
|---|---|
| Endpoint | `POST /jasperReports/reportes` (con el prefijo `/api` si lo proporciona la configuración del entorno) |
| Controller | `BuildReportsApi.generarPdfFactura(Jasper_DTO)` |
| Servicio | `BuildReports.buildPrint(reportName, parameters, connection)` |
| Compilación y caché | `ReportCache.getCompiled(reportName)` → `JRXmlLoader` → `JasperCompileManager` |
| Consulta / DAO | SQL parametrizada del JRXML, ejecutada por Jasper sobre la conexión JDBC del `DataSource` existente |
| DTO | Se reutiliza `Jasper_DTO`: `reportName`, `parameters`, `extencion` |
| Exportación | `ReporteExportService.export(extencion, jasperPrint)` |

No se crea un repositorio ni una proyección adicional: los reportes SQL existentes ya emplean este flujo JDBC. La clasificación queda en SQL, sin agregar lógica de negocio al controller.

Ejemplo de solicitud:

```json
{
  "reportName": "ResumenFacturacion",
  "extencion": "pdf",
  "parameters": {
    "fechaInicio": "2026-09-01",
    "fechaFin": "2026-09-30",
    "razonSocial": "EPMAPA-T",
    "ruc": "RUC_INSTITUCIONAL",
    "dirMatriz": "DIRECCIÓN_INSTITUCIONAL"
  }
}
```

`fechaInicio` y `fechaFin` son parámetros Jasper `java.lang.String` en formato ISO `yyyy-MM-dd`, convertidos a `date` por PostgreSQL. Se comprobó que `facturas.fechacobro` y `feccrea` son columnas **date**, por lo que `BETWEEN` incluye ambos días completos. El solicitante debe enviar fechas válidas y un intervalo ordenado. Un período válido sin registros genera totales cero y porcentajes `N/A`.

## Clasificación y totales

Se verificaron los identificadores del catálogo actual: 1 Contado, 3 Nota Credito, 4 SPI - Transferencia, 5 Tarjeta de Credito, 6 Recaudacion Externa, 7 Cheque y 8 NO USAR. La clasificación usa estos identificadores estables; las formas desconocidas conservan su descripción y quedan visibles para revisión mediante un `LEFT JOIN`.

| Categoría | Regla | Integra TOTAL COBRADO |
|---|---|---|
| EFECTIVO | Contado, estado 1 o 2 | Sí |
| TRANSFERENCIA | SPI, estado 1 | Sí |
| TRANSFERENCIA NO COBRADA | SPI, estado 3 | No |
| RECAUDACIÓN EXTERNA | Forma 6 | Solo estados 1 o 2 |
| NOTAS CRÉDITO | Forma 3 | No; cuadro independiente |
| Tarjeta / Cheque | Formas 5 / 7 | Solo estados 1 o 2 |
| Otras formas o estados | Descripción original o FORMA NO IDENTIFICADA | No, hasta verificar su semántica |

El total requiere fecha de cobro y ausencia de eliminación lógica. Los registros con estados incompatibles quedan visibles con `REVISAR ESTADO`; no se incorporan silenciosamente al total. Los grupos de control pueden mostrar la misma forma en un grupo válido y otro de revisión. Contado estados 1 y 2 válidos producen **una sola fila EFECTIVO**.

`RecaudacionCobroServicio.transferir` asigna simultáneamente estado 3, `pagado = 1` y fecha de cobro. Por eso **ni `pagado = 1` ni una fecha bastan para considerar cobrada una transferencia**. El cobro normal conserva estado 2 en convenios; el estado 1 por sí solo tampoco demuestra pago. La eliminación lógica asigna estado 0. La anulación conserva otros estados y limpia el cobro: se cuenta por `fechaanulacion`, sin inventar un “estado anulado”.

Ejemplo verificado con los datos solicitados:

| Forma | Cantidad |
|---|---:|
| EFECTIVO | 17.814 |
| TRANSFERENCIA | 185 |
| RECAUDACIÓN EXTERNA | 1.466 |
| NOTAS CRÉDITO | 2 |
| TRANSFERENCIA NO COBRADA | 45 |
| TOTAL COBRADO | **19.465** |

Las 45 transferencias pendientes y las dos notas de crédito no incrementan el total. Decisión conservadora aplicada: las notas de crédito quedan separadas de la recaudación efectiva. Este es un total de **facturas**, no de dinero ni de transacciones.

## Bloques disponibles

1. Resumen ejecutivo: creadas, cobradas por fecha de cobro, pendientes de las creadas, SPI pendiente, NC aplicadas y porcentajes por cantidad.
2. Formas de cobro: categorías consolidadas, controles de estado, total cobrado y total SPI pendiente.
3. Estados de las facturas creadas: códigos reales; estados 1 y 2 requieren revisar fecha/pago.
4. Evolución diaria: creadas y cobradas, incluidos días sin movimientos; promedio, mínimo y máximo de cantidad diaria.
5. Pendientes de las creadas: SPI, otras pendientes, total y antigüedad 0–7, 8–15, 16–30, 31–60 y más de 60 días, calculada desde `feccrea` hasta `fechaFin`.
6. Anulaciones y eliminaciones: cantidades y desglose de anulaciones por usuario y motivo.
7. Cajeros: cantidades cobradas por identificador de usuario y forma de cobro, evitando joins que multipliquen facturas.
8. Auditoría: formas ausentes/inexistentes/NO USAR, pagadas sin fecha, tarifa nula/no positiva, estados desconocidos, cobros fuera del intervalo, números repetidos y estados incompatibles. Los controles de incidencia muestran `SIN NOVEDAD` o `REVISAR`; cobros fuera del período son informativos.

## Límites que deben permanecer explícitos

- “Emitidas” usa **fecha de creación `feccrea`**. Incluye los registros creados en `facturas`; no certifica emisión electrónica, autorización SRI ni asignación de número. Las tablas lo indican.
- Cobros del intervalo y creaciones del intervalo son cohortes independientes. La evolución diaria permite compararlas, pero no divide cobros de facturas antiguas entre facturas nuevas para producir un porcentaje engañoso. Los porcentajes de cobro, anulación y NC se calculan sobre las mismas facturas creadas.
- Los porcentajes, pendientes y estados reflejan **el estado actual consultado**. No reconstruyen cómo estaba una factura al cierre histórico. Una factura creada en el período y cobrada después del período puede figurar actualmente como cobrada en el porcentaje de su cohorte.
- La antigüedad y “TOTAL PENDIENTE” abarcan solo la cohorte de creación solicitada, **no toda la cartera histórica**. Las SPI del cuadro de formas se seleccionan por su fecha de registro en `fechacobro`, como la consulta original; pendientes sin esa fecha no entran en ese cuadro.
- “NOTAS CRÉDITO” cuenta facturas con esa forma de cancelación; **no equivale al número de notas de crédito emitidas**. No se inventan número, fecha, motivo o factura original de una NC.
- Las anulaciones son fechas persistidas, no un historial completo de eventos. Una factura anulada y posteriormente recobrada puede aparecer tanto en anulaciones como en cobros. Los bloques no deben sumarse entre sí.
- Duplicados: grupos de números no vacíos repetidos **dentro del alcance**. No busca duplicados en todo el ERP ni considera repetición de un número nulo como duplicado.
- Las inconsistencias se analizan sobre facturas creadas, cobradas, anuladas o eliminadas en el intervalo; no constituyen una auditoría de toda la base.

## Importes económicos: pendientes de conciliación

No se confirmó un campo único que represente el **importe final efectivamente recaudado** para todos los módulos y convenios. `totaltarifa` se recompone como suma de rubros y no demuestra por sí sola recaudación final. El cobro calcula subtotal + interés + IVA y distribuye `valornotacredito`; puede haber cancelaciones mixtas. El reporte existente `FacturasCobradas.jrxml` también reconstruye importes desde rubros, ajustes y valores persistidos, en lugar de sumar únicamente `totaltarifa`.

Por eso esta implementación entrega estadísticas por **cantidad** y omite montos monetarios, tal como permite el requerimiento si no se confirma el total correcto. La cabecera lo advierte. No se presenta tarifa base como dinero ingresado, ni se inventan saldo, valor facturado, porcentaje monetario, ticket promedio o valor anulado/NC. El mínimo y máximo implementados son de cantidad diaria, no de importe de factura. La evolución se entrega en tabla; no se añadió un gráfico monetario con valores sin conciliar.

Para incorporar montos se debe conciliar la fórmula contra los reportes contables vigentes y los convenios, definir qué importe corresponde a emisión y a cobro, y separar crédito aplicado de ingreso efectivo. Las cantidades de formas mixtas NC también requieren esa decisión para un eventual cuadro de caja.

## Validación y ejecución

```powershell
./scripts/test-resumen-facturacion.ps1
```

El ejecutor crea PostgreSQL temporal en `127.0.0.1:55440`, usa datos sintéticos, ejecuta ocho pruebas y detiene la instancia en `finally`. No lee credenciales de producción. Requiere Maven, Java y PostgreSQL local; su directorio de binarios puede configurarse con `-PostgresBin`.

Las ocho pruebas cubren consolidación del ejemplo completo, límites de fecha, reporte vacío, formas desconocidas, separación de SPI pese al indicador de pago, inconsistencias, independencia de cohortes y eliminación lógica con exportación multipágina. Jasper compila el JRXML con las dependencias actuales, ejecuta la SQL real contra PostgreSQL y exporta PDF. Se revisan también imágenes renderizadas por Jasper.

Resultado: **8 pruebas aprobadas**, PDF sintético revisado en dos páginas. También se validó la consulta mediante `EXPLAIN` contra el esquema real, en conexión de solo lectura y sin ejecutar el reporte sobre datos reales.

El proyecto ejecuta proveedores JUnit y TestNG; TestNG puede sobrescribir el XML de esta clase con cero pruebas. El script comprueba el resultado específico de las ocho pruebas JUnit en el log y el código de salida Maven. Ejecutar `mvn test` sin el indicador `RESUMEN_TEST_POSTGRES=true` omite esta integración deliberadamente.

Artefactos locales de revisión, no versionados:

- `target/resumen-facturacion/ResumenFacturacion.original.jrxml`: copia de la plantilla recibida.
- `target/resumen-facturacion/ResumenFacturacion-ejemplo.pdf` y `pagina-*.png`: muestra sintética.
- `target/resumen-facturacion-junit.log`: evidencia de pruebas.

El reporte queda disponible mediante el generador común al empaquetar el backend. No se ha desplegado ni modificado la configuración de permisos o el listado de reportes del frontend.

## SQL final

La siguiente consulta es la misma que contiene `ResumenFacturacion.jrxml`; usa parámetros enlazados `$P{...}`, no concatenación de fechas.


```sql
WITH limites AS (
 SELECT CAST($P{fechaInicio} AS date) AS inicio, CAST($P{fechaFin} AS date) AS fin
), alcance AS (
 SELECT f.*, t.descripcion, t.idformacobro AS forma_valida,
   f.feccrea BETWEEN l.inicio AND l.fin AS emitida_periodo,
   f.fechacobro BETWEEN l.inicio AND l.fin AS cobro_periodo,
   f.fechaanulacion BETWEEN l.inicio AND l.fin AS anulada_periodo,
   f.fechaeliminacion BETWEEN l.inicio AND l.fin AS eliminada_periodo,
   l.inicio, l.fin
 FROM facturas f CROSS JOIN limites l
 LEFT JOIN formacobro t ON t.idformacobro = f.formapago
 WHERE f.feccrea BETWEEN l.inicio AND l.fin
    OR f.fechacobro BETWEEN l.inicio AND l.fin
    OR f.fechaanulacion BETWEEN l.inicio AND l.fin
    OR f.fechaeliminacion BETWEEN l.inicio AND l.fin
), clasificada AS (
 SELECT a.*,
 CASE
   WHEN formapago = 1 AND estado IN (1,2) THEN 'EFECTIVO'
   WHEN formapago = 4 AND estado = 1 THEN 'TRANSFERENCIA'
   WHEN formapago = 4 AND estado = 3 THEN 'TRANSFERENCIA NO COBRADA'
   WHEN formapago = 6 THEN 'RECAUDACIÓN EXTERNA'
   WHEN formapago = 3 THEN 'NOTAS CRÉDITO'
   ELSE COALESCE(NULLIF(BTRIM(descripcion),''),'FORMA NO IDENTIFICADA')
 END AS categoria,
 (estado IN (1,2) AND formapago IN (1,5,6,7) OR estado = 1 AND formapago = 4)
   AND fechacobro IS NOT NULL AND fechaeliminacion IS NULL AND forma_valida IS NOT NULL AS cobrada,
 (estado = 3 OR estado IN (1,2) AND COALESCE(pagado,0) = 0 AND fechacobro IS NULL)
   AND fechaeliminacion IS NULL AS pendiente,
 estado = 3 AND formapago = 4 AND fechaeliminacion IS NULL AS spi_pendiente
 FROM alcance a
), conteos AS (
 SELECT COUNT(*) FILTER (WHERE emitida_periodo) AS emitidas,
 COUNT(*) FILTER (WHERE cobro_periodo AND cobrada) AS cobradas,
 COUNT(*) FILTER (WHERE emitida_periodo AND cobrada) AS emitidas_cobradas,
 COUNT(*) FILTER (WHERE emitida_periodo AND pendiente) AS pendientes,
 COUNT(*) FILTER (WHERE emitida_periodo AND spi_pendiente) AS pendientes_spi,
 COUNT(*) FILTER (WHERE emitida_periodo AND formapago = 3 AND estado IN (1,2) AND fechacobro IS NOT NULL AND fechaeliminacion IS NULL) AS emitidas_notas,
 COUNT(*) FILTER (WHERE anulada_periodo) AS anulaciones,
 COUNT(*) FILTER (WHERE emitida_periodo AND anulada_periodo) AS emitidas_anuladas,
 COUNT(*) FILTER (WHERE cobro_periodo AND spi_pendiente) AS transferencias,
 COUNT(*) FILTER (WHERE cobro_periodo AND formapago = 3 AND estado IN (1,2) AND fechaeliminacion IS NULL) AS notas,
 COUNT(*) FILTER (WHERE eliminada_periodo) AS eliminadas
 FROM clasificada
), diarios AS (
 SELECT d::date AS dia,
 (SELECT COUNT(*) FROM clasificada c WHERE c.feccrea = d::date) AS emitidas,
 (SELECT COUNT(*) FROM clasificada c WHERE c.fechacobro = d::date AND c.cobrada) AS cobradas
 FROM limites l CROSS JOIN LATERAL generate_series(l.inicio::timestamp,l.fin::timestamp,interval '1 day') d
), filas AS (
 SELECT 1 AS bloque, 1 AS orden, 'RESUMEN EJECUTIVO'::text AS seccion,
 'FACTURAS EMITIDAS (fecha de creación)'::text AS concepto, emitidas AS cantidad, ''::text AS indicador FROM conteos
 UNION ALL SELECT 1,2,'RESUMEN EJECUTIVO','TOTAL COBRADO (por fecha de cobro)',cobradas,'' FROM conteos
 UNION ALL SELECT 1,3,'RESUMEN EJECUTIVO','PENDIENTES DE LAS EMITIDAS (estado actual)',pendientes,'' FROM conteos
 UNION ALL SELECT 1,4,'RESUMEN EJECUTIVO','TRANSFERENCIAS PENDIENTES DEL PERÍODO',transferencias,'' FROM conteos
 UNION ALL SELECT 1,5,'RESUMEN EJECUTIVO','NOTAS CRÉDITO APLICADAS (no efectivo)',notas,'' FROM conteos
 UNION ALL SELECT 1,6,'RESUMEN EJECUTIVO','COBRADAS DE LAS EMITIDAS',emitidas_cobradas,
 COALESCE(ROUND(100.0 * emitidas_cobradas / NULLIF(emitidas,0),2)::text || ' %','N/A') FROM conteos
 UNION ALL SELECT 1,7,'RESUMEN EJECUTIVO','ANULACIONES DE LAS EMITIDAS',emitidas_anuladas,
 COALESCE(ROUND(100.0 * emitidas_anuladas / NULLIF(emitidas,0),2)::text || ' %','N/A') FROM conteos
 UNION ALL SELECT 1,8,'RESUMEN EJECUTIVO','NOTAS CRÉDITO DE LAS EMITIDAS',emitidas_notas,
 COALESCE(ROUND(100.0 * emitidas_notas / NULLIF(emitidas,0),2)::text || ' %','N/A') FROM conteos
 UNION ALL SELECT 2,1,'RECAUDACIÓN POR FORMA DE COBRO',categoria,COUNT(*),
 CASE WHEN BOOL_AND(cobrada) THEN 'COBRADO' WHEN BOOL_AND(spi_pendiente) THEN 'PENDIENTE'
 WHEN formapago = 3 THEN 'NO ES EFECTIVO' ELSE 'REVISAR ESTADO' END
 FROM clasificada WHERE cobro_periodo AND fechaeliminacion IS NULL
 GROUP BY categoria,formapago,
 CASE WHEN cobrada THEN 1 WHEN spi_pendiente THEN 2 WHEN formapago = 3 THEN 3 ELSE 4 END
 UNION ALL SELECT 2,90,'RECAUDACIÓN POR FORMA DE COBRO','TOTAL COBRADO',cobradas,'EXCLUYE SPI 3 Y NC' FROM conteos
 UNION ALL SELECT 2,91,'RECAUDACIÓN POR FORMA DE COBRO','TOTAL TRANSFERENCIAS PENDIENTES',transferencias,'NO SUMA AL COBRADO' FROM conteos
 UNION ALL SELECT 3,1,'CONTROL DE ESTADOS (emitidas)',
 'Estado ' || COALESCE(estado::text,'NULL'),COUNT(*),
 CASE WHEN estado = 0 THEN 'ELIMINACIÓN LÓGICA' WHEN estado = 3 THEN 'PENDIENTE'
 WHEN estado IN (1,2) THEN 'VER COBRO / PAGADO' ELSE 'NO RECONOCIDO' END
 FROM clasificada WHERE emitida_periodo GROUP BY estado
 UNION ALL SELECT 4,1,'EVOLUCIÓN DIARIA (cantidades)',to_char(dia,'YYYY-MM-DD'),emitidas,'Cobradas: ' || cobradas FROM diarios
 UNION ALL SELECT 4,90,'EVOLUCIÓN DIARIA (cantidades)','Promedio diario de emitidas',NULL,
 COALESCE(ROUND(AVG(emitidas),2)::text,'N/A') FROM diarios
 UNION ALL SELECT 4,91,'EVOLUCIÓN DIARIA (cantidades)','Mínimo / máximo diario de emitidas',NULL,
 COALESCE(MIN(emitidas)::text || ' / ' || MAX(emitidas)::text,'N/A') FROM diarios
 UNION ALL SELECT 5,1,'CONTROL DE PENDIENTES (emitidas)','Transferencias pendientes',pendientes_spi,'ESTADO ACTUAL' FROM conteos
 UNION ALL SELECT 5,2,'CONTROL DE PENDIENTES (emitidas)','Otras pendientes',pendientes-pendientes_spi,'ESTADO ACTUAL' FROM conteos
 UNION ALL SELECT 5,3,'CONTROL DE PENDIENTES (emitidas)','TOTAL PENDIENTE',pendientes,'ESTADO ACTUAL' FROM conteos
 UNION ALL SELECT 5,
 CASE WHEN fin-feccrea <= 7 THEN 10 WHEN fin-feccrea <= 15 THEN 20 WHEN fin-feccrea <= 30 THEN 30 WHEN fin-feccrea <= 60 THEN 40 ELSE 50 END,
 'ANTIGÜEDAD (pendientes de las emitidas)',
 CASE WHEN fin-feccrea <= 7 THEN '0 - 7 días' WHEN fin-feccrea <= 15 THEN '8 - 15 días'
 WHEN fin-feccrea <= 30 THEN '16 - 30 días' WHEN fin-feccrea <= 60 THEN '31 - 60 días' ELSE 'Más de 60 días' END,
 COUNT(*),'ESTADO ACTUAL'
 FROM clasificada WHERE emitida_periodo AND pendiente GROUP BY 2,4
 UNION ALL SELECT 6,1,'ANULACIONES Y ELIMINACIONES','Anulaciones registradas en el período',anulaciones,'' FROM conteos
 UNION ALL SELECT 6,2,'ANULACIONES Y ELIMINACIONES','Eliminaciones registradas en el período',eliminadas,'' FROM conteos
 UNION ALL SELECT 6,3,'ANULACIONES Y ELIMINACIONES',
 'Usuario ' || COALESCE(usuarioanulacion::text,'sin registrar') || ' / ' || LEFT(COALESCE(NULLIF(razonanulacion,''),'Sin motivo'),60),COUNT(*),'ANULACIÓN'
 FROM clasificada WHERE anulada_periodo GROUP BY usuarioanulacion,LEFT(COALESCE(NULLIF(razonanulacion,''),'Sin motivo'),60)
 UNION ALL SELECT 7,1,'CONTROL POR CAJERO Y FORMA',
 'Usuario ' || COALESCE(usuariocobro::text,'sin registrar') || ' / ' || categoria,COUNT(*),'COBRADO'
 FROM clasificada WHERE cobro_periodo AND cobrada GROUP BY usuariocobro,categoria
 UNION ALL SELECT 8,1,'CONTROL DE INCONSISTENCIAS','Sin forma de pago',COUNT(*) FILTER (WHERE formapago IS NULL),'REVISAR SI > 0' FROM clasificada
 UNION ALL SELECT 8,2,'CONTROL DE INCONSISTENCIAS','Forma inexistente o deshabilitada (NO USAR)',COUNT(*) FILTER (WHERE formapago IS NOT NULL AND (forma_valida IS NULL OR UPPER(BTRIM(descripcion)) = 'NO USAR')),'REVISAR SI > 0' FROM clasificada
 UNION ALL SELECT 8,3,'CONTROL DE INCONSISTENCIAS','Pagadas sin fecha (excluye SPI pendiente)',COUNT(*) FILTER (WHERE pagado = 1 AND fechacobro IS NULL AND estado IN (1,2)),'REVISAR SI > 0' FROM clasificada
 UNION ALL SELECT 8,4,'CONTROL DE INCONSISTENCIAS','Tarifa nula o <= 0 (no es valor final)',COUNT(*) FILTER (WHERE totaltarifa IS NULL OR totaltarifa <= 0),'REVISAR SI > 0' FROM clasificada
 UNION ALL SELECT 8,5,'CONTROL DE INCONSISTENCIAS','Estados no reconocidos',COUNT(*) FILTER (WHERE estado IS NULL OR estado NOT IN (0,1,2,3)),'REVISAR SI > 0' FROM clasificada
 UNION ALL SELECT 8,6,'CONTROL DE INCONSISTENCIAS','Emitidas con cobro fuera del período',COUNT(*) FILTER (WHERE emitida_periodo AND fechacobro IS NOT NULL AND NOT cobro_periodo),'INFORMATIVO' FROM clasificada
 UNION ALL SELECT 8,7,'CONTROL DE INCONSISTENCIAS','Números repetidos dentro del alcance',COUNT(*),'REVISAR SI > 0'
 FROM (SELECT nrofactura FROM clasificada WHERE NULLIF(BTRIM(nrofactura),'') IS NOT NULL GROUP BY nrofactura HAVING COUNT(*) > 1) duplicadas
 UNION ALL SELECT 8,8,'CONTROL DE INCONSISTENCIAS','Cobros con estados incompatibles',COUNT(*) FILTER (WHERE cobro_periodo AND NOT COALESCE(cobrada,FALSE) AND NOT COALESCE(spi_pendiente,FALSE) AND formapago IS DISTINCT FROM 3 AND fechaeliminacion IS NULL),'REVISAR SI > 0' FROM clasificada
)
SELECT bloque, orden, seccion, concepto, cantidad,
 CASE WHEN bloque = 8 AND indicador = 'REVISAR SI > 0'
 THEN CASE WHEN cantidad = 0 THEN 'SIN NOVEDAD' ELSE 'REVISAR' END
 ELSE indicador END AS indicador
 FROM filas ORDER BY bloque,orden,concepto
```
