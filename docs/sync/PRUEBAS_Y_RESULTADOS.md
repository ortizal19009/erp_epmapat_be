# Pruebas y resultados ? 2026-10-13

## Backend

29 pruebas seleccionadas, cero fallos/errores/omisiones: contrato mobile 3, excepciones tracking 4, fotos abonado 5, persistencia trazabilidad 1, recibos lectura/foto 2, validador GPS 7, PostgreSQL 4, registro tracking 1 y WebSocket 2.

Comando: mvn -q "-Dtest=*Mobile*,*Tracking*,*Lectura*,*Storage*,*Audit*,AbonadoFotosTest" test. WAR: mvn -q -DskipTests package. Compilaci?n en copia aislada temporal para evitar interferencias de compilaci?n incremental del IDE. No se us? una conexi?n de producci?n.

MobileSyncPostgresTest levanta postgres:16-alpine ef?mero mediante Testcontainers 1.21.4. Verifica concurrencia con una ?nica sesi?n, reenv?o concurrente con un ?nico recibo/efecto, rollback y aplicaci?n repetida del SQL conservando una lectura. Recibos usan servicio transaccional real y SQL real; la escritura de negocio/auditor?a en esa prueba es un sustituto que escribe sentinelas. No equivale a una prueba integral con todos los datos y servicios del ERP.

## Android

Comando: gradlew.bat testDebugUnitTest connectedDebugAndroidTest assembleRelease.

16 pruebas JVM: pol?tica de fallos, confirmaciones por operaci?n, validaci?n de trazabilidad y recuperaci?n. Siete pruebas instrumentadas en emulador Android API 36.1: migraci?n desde v12 hasta v17 preservando lectura/foto/cola, alias, recuperaci?n por punto original, merges sin sobrescribir trabajo offline, lease y confirmaci?n separada, acuse antiguo frente a edici?n nueva y registro concurrente tras timeout con 1000 puntos pendientes. Una de las siete es la prueba b?sica de ejemplo. El registro de sesi?n usa API simulada y Room real.

La prueba se ejecuta en userdata temporal independiente; no reinstala ni borra una app de trabajo. APK release sin firma; se necesita el certificado original para actualizar celulares.

## L?mites y piloto pendiente

No se midi? un antes/despu?s de rendimiento. Se agregaron m?tricas de cantidad, confirmados/rechazados y duraci?n con percentiles p50/p95 para observarlas en el despliegue. La pantalla muestra pendientes, antig?edad, ?ltima sincronizaci?n y reparaciones GPS.

Falta piloto con celular f?sico actualizado con firma original, corte de red contra backend desplegado y Nextcloud real. Verificar especialmente cierre de app durante upload/foto, respuesta perdida, 429, renovaci?n de login, migraci?n con datos reales y la lectura 3032370. No se modific? la base de producci?n ni se corrigieron manualmente sus jornadas. El ?xito de pruebas no recupera datos que versiones previas hayan borrado.
