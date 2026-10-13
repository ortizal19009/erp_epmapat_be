# Lecturas sin coordenadas GPS - 1.1.7 (versionCode 9)

## Comportamiento autorizado

Si una lectura antigua carece de coordenadas validas, se pueden confirmar sus datos sin exigir una sesion GPS. Mobile envia allowMissingGps=true con los campos de GPS omitidos y el operationId persistido. No modifica la captura original del outbox. El servidor exige lector coincidente y operationId; la via normal con coordenadas validas conserva sus validaciones.

El backend actualiza el dato de lectura y marca readingTraceStatus=GPS_PENDING. Preserva sesion, coordenadas, precision, fecha GPS y distancia anteriores, si existen; esos datos anteriores no certifican la captura actual. La auditoria indica GPS_PENDING y el recibo impide repetir la escritura. Una captura valida posterior se procesa con validacion completa y queda GPS_VALID.

La app persiste GPS_PENDING en Room y lo mantiene al descargar el DTO. Cambios pendientes muestra la cantidad de lecturas guardadas con GPS pendiente. GPS_PENDING no bloquea la confirmacion de los datos ni la descarga posterior. Las fotos siguen requiriendo su acuse propio. No se inventan coordenadas actuales, de otro punto o del medidor. No se afirma que una lectura sin GPS tenga trazabilidad GPS completa.

Los errores anteriores de coordenadas se vuelven a evaluar durante sincronizacion; el boton manual tambien reevalua errores antiguos genericos. No se omiten conflictos de propietario, errores de negocio o confirmaciones incompletas.

## Despliegue manual

1. Respaldar PostgreSQL. Aplicar src/main/resources/sql/2026-10-13_reading_gps_pending.sql en la base del backend antes de publicar el WAR. Los scripts mobile_sync_receipts e historical_tracking_days siguen siendo requisitos si aun no se aplicaron.
2. Desplegar target/sync-review/erp_epmapat-v0.1.war actualizado.
3. Actualizar el celular con EpmpatMobile/app/build/outputs/apk/release/EpmpatMobile-1.1.7-piloto-debug-signed.apk, firmado con la misma clave debug de esta computadora. No desinstalar ni borrar datos.
4. Pulsar Sincronizacion inteligente. Verificar que 3035743 actualiza sus datos y queda GPS_PENDING si carece de coordenadas. Comprobar el dato de lectura y estado en backend; no tratar GPS_PENDING como GPS validado.

Room pasa de 18 a 19 agregando solamente readingTraceStatus. Las migraciones anteriores siguen disponibles. Un rollback Android debe entender esquema 19 y tener versionCode superior; conservar la columna nueva en PostgreSQL.

## Pruebas

34 pruebas backend seleccionadas sin fallos: aceptacion explicita sin GPS, comprobacion de propietario/operacion, validacion normal con coordenadas, conservacion de GPS previo y cinco pruebas en PostgreSQL real. Los SQL se ejecutaron dos veces sobre una base efimera.

16 pruebas JVM y 13 instrumentadas correctas. La prueba adicional ejecuta el repositorio real con Room y APIs simuladas, confirma una lectura sin GPS ni sesion, conserva captura local y persiste GPS_PENDING. Migracion desde v12 hasta v19 conserva lectura, foto y outbox.

No se inspeccionaron los 424 registros reales ni se desplego a produccion. Piloto fisico pendiente. Evidencia: app/build/sync-review/gps-pending-build.log y gps-pending-device-tests.log; backend target/sync-review/gps-pending-tests.log.
