# Reporte Refacturaciones por fechas

El endpoint `/jasperReports/reportes` utiliza el `.jasper` almacenado en `reportesjr`.
Editar solamente el JRXML de resources no cambia ese reporte almacenado.

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
manualmente el `.jasper` de la tabla para este endpoint. La primera ejecución compila;
las siguientes reutilizan esa compilación.

Revisar los índices existentes antes de aplicar
src/main/resources/sql/2026-10-16_refacturaciones_indices.sql. Es un script opcional,
no automático; CREATE INDEX CONCURRENTLY debe ejecutarse fuera de una transacción.
Índices equivalentes con otros nombres deben reutilizarse, evitando duplicarlos.

Repetir el reporte 2026-09-01 a 2026-09-30 y revisar el log
`Reporte Refacturaciones generado paginas=... duracionMs=...`.
Ese tiempo mide consultas y llenado Jasper después de obtener la conexión; no incluye
espera del pool, compilación inicial, exportación ni transferencia al navegador.
