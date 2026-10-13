# Contratos y estados ? 2026-10-13

## Lecturas y fotos

POST /lecturas/mobile/upload conserva array, query usumodi y respuesta HTTP 200 por lote. Cada results debe confirmar posici?n, ID, operationId, estado y c?digo HTTP. El HTTP del lote no confirma todos sus elementos. PUT /lecturas/{id}/mobile admite el mismo operationId opcional. Los clientes antiguos sin clave siguen por la ruta compatible.

operationId es un UUID persistido por captura, no por intento. Reenviar la misma clave y contenido devuelve el recibo sin repetir escritura ni auditor?a. Reutilizarla con otro lector, contenido o tipo devuelve conflicto 409. El servidor serializa operaci?n y lectura con advisory locks PostgreSQL dentro de la transacci?n; lectura, auditor?a y recibo se confirman juntos. WebSocket se notifica despu?s del commit y su fallo no revierte una lectura confirmada.

La foto tiene otra clave estable derivada de la operaci?n de lectura. Se confirma dataConfirmed antes de subirla; un fallo de foto no vuelve a guardar la lectura. El recibo devuelve una ruta remota no vac?a. Local y Nextcloud usan nombre estable con checksum. El almacenamiento externo no participa en la transacci?n SQL: puede quedar un objeto sin recibo tras rollback, pero su reintento usa el mismo nombre.

## Sesiones y trazabilidad

POST /tracking/start adopta el UUID propuesto; no genera un segundo UUID. confirmIdentity=true activa validaci?n de lector, dispositivo, ruta y jornada. La app persiste confirmedRemoteId, remoteConfirmedAt e identidad confirmada, compartidos entre workers. Timeout conserva el ID para reintentar; no se env?a la lectura antes del acuse v?lido. Registro concurrente se serializa en servidor. finish es idempotente para nuevos clientes. Puntos y eventos llevan identificadores estables.

No se cambia fecha, coordenadas ni jornada para lograr aceptaci?n. Una referencia antigua solo se recupera si existe una ?nica coincidencia del punto original por instante, lector, fecha, coordenadas y precisi?n; en otro caso NEEDS_REPAIR conserva la captura. SESSION_NOT_REGISTERED permite una reconciliaci?n autom?tica con el mismo ID; repetir el rechazo requiere revisi?n.

readingSource y readingCaptureAgeMillis son opcionales para compatibilidad. Capturas nuevas usan CAPTURED y antig?edad real de 0 a 30000 ms; datos antiguos nulos representan procedencia desconocida. La conversi?n de jornada usa America/Guayaquil. Una captura 2026-10-12 vinculada a jornada 2026-09-14 requiere recuperar su sesi?n original, aunque haya puntos pendientes.

## Cola persistente Android

| Estado | Acci?n |
|---|---|
| PENDING / ERROR heredado | Elegible para env?o |
| IN_FLIGHT | Captura reclamada con lease; cierre del proceso se recupera al vencer |
| RETRY_WAIT | Reintentar cuando llegue nextAttemptAt |
| AUTH_REQUIRED | Pausar; ingresar con el lector correspondiente |
| NEEDS_REPAIR | Conservar y mostrar diagn?stico; reintento manual tras revisi?n |
| SENT | Conservar confirmaci?n local |

Red, 429 y 5xx usan espera exponencial con jitter; tras ocho intentos pasan a revisi?n. Lecturas/fotos respetan Retry-After; tracking usa backoff de WorkManager y l?mite persistente de intentos. No se promete que todos los workers interpreten Retry-After. Confirmaciones/fallos se aplican solo si coinciden payload y operationId; una edici?n durante env?o queda pendiente con una nueva clave. Clientes y georreferencias requieren acuse propio, pero no tienen recibos SQL equivalentes al de lecturas: no se afirma deduplicaci?n completa de sus efectos de auditor?a.

Descargas combinan registros dentro de Room y protegen todos los estados distintos de SENT. No vac?an cat?logos. Pendientes no resueltos detienen la descarga operativa. Cambiar de usuario con pendientes exige volver a la cuenta original; no borra la cola. Tipos sin uploader quedan en revisi?n asistida.

No se incorpora cursor incremental, control de versi?n entre dispositivos ni resoluci?n autom?tica de dos capturas nuevas sobre la misma lectura. No hubo cambios necesarios en frontend web.
