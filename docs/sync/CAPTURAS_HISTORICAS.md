# Capturas historicas - version 1.1.6 (versionCode 8)

## Comportamiento

La fecha de sincronizacion y la jornada activa no sustituyen la fecha de captura. Cuando una lectura GPS valida esta ligada a una sesion antigua de otro dia, se crea una sesion historica por fecha Guayaquil con sourceSessionId apuntando a la sesion original. Se conservan lector, dispositivo y ruta. El UUID es determinista por sesion original y fecha; reintentos y concurrencia recuperan la misma sesion.

No se modifica la jornada original ni se inventan coordenadas, precision o fecha. La lectura conserva su operationId y sus valores; solo se normaliza su enlace a la sesion historica. El backend registra el origen y comprueba identidad, fecha de inicio y UUID de recuperacion. Mobile confirma primero la sesion original y luego la historica antes de enviar la lectura.

Los puntos pendientes de una sesion mezclada se agrupan por su fecha de captura para enviarlos a sesiones historicas. Puntos y eventos mantienen sus identificadores originales para deduplicar respuestas perdidas. Los puntos conservan capturedAt, incluida precision de milisegundos en el envio. No se mueven ni se borran puntos locales; se marca synced unicamente despues del acuse. Los puntos ya enviados no se duplican ni se reasignan retroactivamente en el servidor.

Los errores conocidos de fecha GPS se vuelven a evaluar al sincronizar. El boton manual tambien reevalua los antiguos errores genericos de dependencia. Una captura con sesion original ausente, GPS invalido o identidad conflictiva sigue pendiente con su motivo. No se desactiva la validacion GPS.

## Actualizacion manual - orden obligatorio

1. Respaldar la base del backend.
2. Aplicar src/main/resources/sql/2026-10-13_historical_tracking_days.sql en la misma base del backend. La migracion principal 2026-10-13_mobile_sync_receipts.sql sigue siendo requisito si no se completo previamente.
3. Desplegar target/sync-review/erp_epmapat-v0.1.war actualizado. El WAR incorpora sourceSessionId; no usar el APK nuevo con el backend anterior.
4. Actualizar el celular con EpmpatMobile/app/build/outputs/apk/release/EpmpatMobile-1.1.6-piloto-debug-signed.apk, firmado con la misma clave debug de esta computadora. No desinstalar ni borrar almacenamiento. Room migra 17 a 18 agregando solo sourceSessionId; la migracion desde v12 tambien esta probada.
5. Pulsar Sincronizacion inteligente y comprobar confirmaciones en backend, inicialmente con un celular. Captura GPS 2026-10-12 vinculada a sesion 2026-10-06 debe conservar la captura y quedar enlazada a una sesion historica del dia 12 cuyo origen sea la del dia 6.

Verificacion de SQL:

```sql
SELECT current_database(), table_schema, column_name
FROM information_schema.columns
WHERE table_name='tracking_sessions' AND column_name='source_session_id';
```

## Validacion y limites

31 pruebas backend correctas, incluidas cinco con PostgreSQL real y el registro historico estable conservando la jornada original. Dos pruebas de registro comprueban compatibilidad e identidad de recuperacion. Ambos scripts se ejecutaron dos veces en PostgreSQL efimero.

16 pruebas JVM y 12 instrumentadas correctas: migracion con lecturas/fotos/cola, upload real del repositorio con Room y API simulada, recuperacion de fecha antigua y separacion de dias con concurrencia. No equivale a haber inspeccionado las 424 capturas del telefono. El piloto fisico y almacenamiento remoto real siguen pendientes.

Rollback: mantener source_session_id en PostgreSQL; una app de rollback debe soportar Room 18 y tener versionCode mayor. No reinstalar una version que solo entiende Room 17 ni borrar la base.

No se aplico SQL ni se hizo despliegue en produccion durante este trabajo.
