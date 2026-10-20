# Reporte Refacturaciones por fechas

El endpoint de la pantalla `/jasperReports/reportes` utiliza el JRXML del classpath
mediante `BuildReports.buildPrint` y `ReportCache`. Requiere desplegar el backend
con el recurso actualizado. El endpoint alternativo `/reporteJasper/sql` utiliza
el `.jasper` almacenado en `reportesjr`.

Para `reportName=Refacturaciones`, `RefacturacionesReportOptimizer` conserva el diseño
almacenado y sustituye las consultas principal, RefFacturas y RefRubros por las
versiones del JRXML versionado. La compilación se reutiliza por SHA-256 del `.jasper`;
una actualización del archivo en BD invalida la entrada. No se guardan resultados
ni PDFs en caché. Los demás reportes siguen su flujo existente.

La consulta de detalle limita las sumas de rubros a las facturas originales y nuevas
del período, en lugar de agrupar todo el historial. Conserva exclusiones de rubros
5/165, estados de rubros y relaciones de las consultas originales. El rango de fechas
incluye el día final completo mediante límite superior exclusivo del día siguiente.

## Verificación

Ejecutar `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-refacturaciones.ps1`.
El script inicia PostgreSQL temporal en 127.0.0.1:55441, sin utilizar configuración
de producción. Verifica conservación del diseño, caché e invalidación, igualdad de
resultados con la consulta anterior para iguales límites temporales, rubros excluidos,
último día del mes y generación PDF. El cluster se detiene al finalizar.

Una ejecución local con 100.000 facturas/lecturas/rubros históricos y dos facturas
seleccionadas de septiembre de 2026 midió 112,856 ms en la consulta original y
0,265 ms en la optimizada (EXPLAIN ANALYZE, con índices, PostgreSQL 18).
Es una prueba sintética, no una medida del servidor 192.168.0.88 ni del tiempo total
del PDF. Los planes están en target/refacturaciones-original-plan.txt y
target/refacturaciones-optimized-plan.txt.

## Aplicación

Compilar y desplegar el backend erp_epmapat_be actualizado. No hace falta reemplazar
manualmente el `.jasper` de la tabla para estos endpoints. La primera ejecución compila;
las siguientes reutilizan esa compilación.

Revisar los índices existentes antes de aplicar
src/main/resources/sql/2026-10-16_refacturaciones_indices.sql. Es un script opcional,
no automático; CREATE INDEX CONCURRENTLY debe ejecutarse fuera de una transacción.
Índices equivalentes con otros nombres deben reutilizarse, evitando duplicarlos.

Repetir el reporte 2026-09-01 a 2026-09-30 y revisar el log
`Reporte Refacturaciones generado paginas=... duracionMs=...`.
Ese tiempo mide consultas y llenado Jasper después de obtener la conexión; no incluye
espera del pool, compilación inicial, exportación ni transferencia al navegador.

## Corrección del 19 de octubre de 2026

El reporte ahora incluye únicamente facturas con una relación explícita en
emisionindividual y lectura nueva existente. Las bajas comunes sin ese vínculo
no se presentan como refacturaciones de importe cero. Se conserva el filtro por
fecha de eliminación de la factura original. No se crean ni cambian vínculos.

Se corrigieron los caracteres dañados de los encabezados y el pie de página.
Un importe ausente se muestra como N/D; un importe real igual a cero sigue
mostrándose como cero. El PDF incluye una explicación de N/D. Los importes y
totales se verifican extrayendo el texto del PDF de prueba, incluyendo diferencias
negativas y facturas nuevas sin rubros. Esta corrección sólo afecta al reporte,
no al proceso de refacturación de cuentas o facturas.

La revisión de septiembre de 2026 en la base configurada fue de solo lectura:
68 facturas eliminadas, 14 con factura nueva vinculada y 54 sin ese vínculo.
Las 14 facturas nuevas tenían rubros computables con valores distintos de cero.


## Importes e intereses

Las sumas de Facturado, Refacturado y el detalle de rubros excluyen los rubros
identificados como Interés/Intereses en el catálogo, incluyendo variantes con
acentos, mayúsculas y descripciones como «Interés (GAD-MT)». Se mantienen las
exclusiones anteriores de los códigos 5 y 165 (IVA). `Interferencia` no coincide
con el filtro de intereses.

Si la factura original no tiene ningún rubro y su cabecera totaltarifa confirma
cero, Facturado muestra 0,00. Una cabecera no nula y distinta de cero sin detalle
no sustituye los rubros: se conserva N/D para evitar inventar un importe o incluir
intereses de la cabecera.

La factura 3074037 (cuenta 3800), eliminada con razón ERROR SISTEMA ERP, cumple
el primer caso. La consulta de septiembre de 2026, ejecutada en solo lectura,
devuelve 14 filas con Facturado informado: total original 7.135,69 y nuevo 295,03.
No se encontraron rubros de intereses en esas 14 facturas originales.
Las pruebas PostgreSQL/Jasper verifican intereses con varios códigos, conservación
de otros rubros, cero confirmado y datos ausentes sin sustitución de importes.


## Resumen final

El cuadro final reutiliza los resultados de los datasets, sin consultas adicionales.
Cuenta cuentas distintas, facturas originales distintas y tipos de rubro distintos
presentes en el detalle (no filas individuales de rubroxfac). Muestra los totales
facturado y refacturado sin intereses ni IVA y su resta como diferencia. Los N/D
no se incluyen en las sumas. Se conserva el aviso de importes disponibles.
El endpoint de reportes almacenados recibe el mismo resumen manteniendo el detalle.
Las pruebas PDF comprueban una cuenta con dos facturas, dos tipos de rubro y los
importes 30,00 / 22,00 / -8,00.


## Verificacion con datos reales: septiembre de 2026

Se genero el PDF del 2026-09-01 al 2026-09-30 con la conexion configurada en
transaccion de solo lectura: 12 cuentas, 14 facturas originales, 9 tipos de rubro,
7.135,69 facturado, 295,03 refacturado y -6.840,66 de diferencia, sin importes de
factura incompletos. El PDF tiene dos paginas y el resumen aparece en la ultima.

La verificacion detecto que RefRubros omitia conceptos exclusivos de facturas nuevas,
incluidos 8,83 de la cuenta 3800. Se reemplazo el cruce por rubro de cada factura por
agregaciones independientes de originales y nuevas, unidas por codigo de rubro.
Esto incluye conceptos nuevos y evita multiplicar importes con varias filas del
mismo rubro. Ambas tablas ahora coinciden en sus totales. La prueba de regresion
incluye una factura original sin rubros y una nueva con un concepto de 8,83.
No se modificaron registros ni el proceso de refacturacion.


## Ampliacion a facturas eliminadas sin nueva emision

Por solicitud del usuario, el reporte incluye tambien bajas sin un registro de
reemplazo en emisionindividual. En estas filas Refacturado es 0 y Diferencia es
0 menos Facturado. Un vinculo existente con datos faltantes conserva N/D, para
no confundir un problema de datos con una baja sin reemplazo. Los totales por
rubro y el resumen incorporan las bajas, sin cambiar los procesos ni registros.
Los conteos finales se rotulan Cuentas incluidas y Facturas eliminadas.

Verificacion de solo lectura del 2026-09-01 al 2026-09-30: 14 cuentas, 68 facturas
(14 con nueva factura y 54 sin nueva), 15 tipos de rubro, Facturado 17.531,73,
Refacturado 295,03 y Diferencia -17.236,70. Ambas tablas coinciden. El PDF real
se genero y reviso en sus cuatro paginas. Las pruebas PostgreSQL/Jasper verifican
una baja de 10 sin reemplazo con nuevo 0 y diferencia -10, y su suma en rubros.
