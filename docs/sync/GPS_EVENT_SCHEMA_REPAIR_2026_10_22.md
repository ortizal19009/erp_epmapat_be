# Reparacion de client_event_id en tracking

## Causa comprobada

El backend consulta GpsGapEvent.clientEventId al deduplicar un lote, pero PostgreSQL informa que gps_gap_events.client_event_id no existe. La misma propiedad existe en RouteDeviationEvent. application.properties configura ddl-auto=none, por lo que las entidades JPA no agregan columnas automaticamente.

El script src/main/resources/sql/2026-10-13_mobile_sync_receipts.sql ya incluye estas columnas e indices. Sin embargo, TrackingSchemaMigration, ejecutado al arrancar, solo aseguraba source_session_id y client_point_id. Se agregaron las dos columnas client_event_id y sus indices unicos con IF NOT EXISTS, compatibles con el script existente.

No se rellenan identificadores inventados para historicos, no se borran eventos y no se deshabilita la deduplicacion. PostgreSQL permite multiples NULL en estos indices, preservando eventos antiguos sin clave de cliente.

## Validacion

TrackingSchemaMigrationTest verifica las ocho sentencias y su repeticion. TrackingSchemaMigrationPostgresTest crea tablas antiguas sin los campos, conserva eventos existentes, ejecuta dos veces la migracion y comprueba tanto los historicos NULL como el rechazo de claves duplicadas. Se ejecutan tambien TrackingRegistrationTest y MobileSyncPostgresTest.

## Aplicacion al servidor

Se requiere compilar/desplegar el backend actualizado y reiniciarlo con un usuario de base que pueda alterar estas tablas y crear los indices. La migracion se ejecuta al arrancar. No se modifico ninguna base operativa desde esta sesion.

Si la migracion falla por permisos o por duplicados previamente existentes, el arranque debe fallar y exponer la causa; no anunciar exito ni eliminar datos automaticamente. No habilitar ddl-auto=create/create-drop para resolver este problema.

El mensaje "Se guardaron puntos" aparece antes de terminar el metodo transaccional: no confirma COMMIT. Un error posterior en eventos revierte el lote; el movil debe mantenerlo pendiente y reintentar tras la reparacion del esquema. Los cierres WebSocket EOF/1006 del mismo archivo son eventos diferentes y no crean una columna faltante.
