# Plan de ejecución para agente de desarrollo — Sincronización EpmpatMobile ↔ ERP EPMAPA-T

**Estado:** especificación de implementación sujeta a inspección del código.  
**Objetivo:** corregir rechazos de lecturas por sesión GPS, robustecer sincronización Offline-First y mejorar eficiencia sin pérdida de datos ni regresiones.  
**Ámbito:** aplicación Android EpmpatMobile (Kotlin, Room, Retrofit, WorkManager) y backend ERP EPMAPA-T (Spring Boot, PostgreSQL).  
**Prioridad:** integridad de lecturas y evidencia fotográfica; tracking asíncrono sin bloquear la operación.

> **Instrucción al agente:** actúa como ingeniero responsable de diagnóstico, implementación, pruebas y documentación. **No asumas** que las clases, rutas o columnas propuestas existen con esos nombres exactos: compruébalas en el repositorio. No implementes cambios destructivos, no borres outbox pendiente y no desactives validaciones GPS para ocultar errores. Registra los hallazgos y las decisiones antes de modificar contratos.

## 0. Contexto confirmado por la documentación entregada

### Flujo y componentes actuales

| Proceso | Endpoint | Cliente / función indicada | Regla actual |
|---|---|---|---|
| Lecturas, upload | `POST /api/lecturas/mobile/upload` | `LecturasApi.uploadLecturasBatch(body, userId)`; `LecturasSyncRepository.uploadPendingLecturasLocked()` | Prioridad 1; `TraceabilityContract.validate()` valida UUID v4, coordenadas y precisión |
| Fotos, upload | `POST /api/lecturas/{id}/foto` | `LecturasApi.uploadFoto()`; `LecturasSyncRepository.uploadPendingLecturasLocked()` | JPEG con marca de agua y respaldo local |
| Inicio tracking | `POST /api/tracking/start` | `TrackingApi.startSession` | La app actualmente permite continuar con lecturas si falla esta notificación |
| Puntos GPS | `POST /api/tracking/points/batch` | `TrackingSyncWorker.doWork()` | Lotes de hasta 50 puntos; asíncrono |
| Fin tracking | `POST /api/tracking/finish` | `TrackingApi` | Cierre de jornada |
| Clientes | `POST /api/clientes/upsert` | `SyncRepository.uploadPending()` | Cambios de clientes en campo |
| Geolocalización abonados | `POST /api/abonados/geo/upload` | `AbonadosSyncRepository.uploadPendingGeo()` | Actualización de ubicación de medidor |
| Descarga integral | `GET /api/mobile/sync/all-assigned` | `MobileSyncRepository.downloadOperationalData()`; `performSmartSync()` | Emisión, rutas, clientes, abonados, lecturas, categorías, novedades y pliegos |
| Descarga lecturas | `GET /api/lecturas/by-rutas` o `/api/lecturas/by-usuario-emision` | `LecturasSyncRepository.syncAssignedRutas()` | Merge preservando cambios locales pendientes |

La app utiliza **Room + Outbox**, prioriza lecturas y fotos y ejecuta GPS mediante WorkManager. Existen mecanismos de recuperación de coordenadas y reconciliación de jornada según fecha de captura.

### Síntomas observados en logs previamente revisados

- Respuestas `400 BAD_REQUEST`: **«La sesion GPS debe usar su UUID remoto»** en subida de lecturas.
- Inicio de sesiones de tracking repetido en intervalos muy cortos; **no implica por sí mismo** que existan registros duplicados en base de datos.
- Eventos WebSocket `SocketTimeoutException: Write timeout` y cierre 1006; la entrega crítica de lecturas no debe depender de WebSocket.

**Hipótesis, aún no verificada:** el móvil envía un UUID local donde el backend exige el UUID remoto, o continúa el upload tras fallar el registro/reconciliación de la sesión. Confirmar mediante código, payloads sanitizados y respuesta real del backend.

## 1. Reglas obligatorias de ejecución

1. **Primero inspeccionar, luego modificar.** Detectar versiones reales, entidades, migraciones, DTO, servicios, validadores, índices, autenticación y llamadas de red.
2. **Preservar compatibilidad:** no cambiar endpoints, cuerpos, códigos de respuesta o semántica sin revisar consumidores; preferir ampliaciones compatibles y versionado cuando sea necesario.
3. **Nunca perder trabajo offline:** no eliminar ni marcar `SYNCED` una operación antes de confirmación inequívoca del servidor y persistencia local atómica.
4. **No inventar UUID remotos:** solo utilizar UUID devueltos/confirmados por backend; un UUID local válido sintácticamente no demuestra existencia remota.
5. **No eliminar la validación GPS** en backend ni falsificar coordenadas, precisión, hora o fuente de captura.
6. **Distinguir GPS capturado y GPS de respaldo:** guardar `source` (`CAPTURED`, `LAST_TRACK_POINT`, `METER_LOCATION`, etc.), `capturedAt`, precisión real y antigüedad; aplicar políticas de negocio existentes y señalar cambios propuestos para aprobación. La coordenada del medidor no debe hacerse pasar por ubicación efectiva del lector.
7. **No reintentar indefinidamente errores definitivos:** usar estados persistidos, razones y posibilidad de reparación.
8. **No registrar secretos ni datos sensibles:** tokens, imágenes, ubicación exacta y datos personales fuera de logs; usar identificadores de correlación y redacción.
9. **Separar dependencia de sesión de carga de puntos:** el UUID remoto puede ser prerrequisito de una lectura, pero la transmisión masiva de puntos GPS no debe bloquearla.
10. **No tocar datos de producción ni ejecutar migraciones irreversibles** sin respaldo, revisión y aprobación explícita.

## 2. Fase 1 — Auditoría y reproducción (sin cambios funcionales)

### 2.1 Localizar e inspeccionar

**Android**
- `LecturasSyncRepository.kt`: `uploadPendingLecturasLocked()`, `uploadPendingLecturas()`, armado de payload, retries y confirmaciones.
- `TraceabilityContract.kt`: `validate()`, `confirm()`, UUID local/remoto, precisión y fallback GPS.
- `TrackingApi`, `TrackingSyncWorker.kt`, repositorio/DAO/entidades de sesiones y puntos GPS.
- `MobileSyncRepository.kt`, `SyncRepository`, `AbonadosSyncRepository`, `LecturasApi`, DTO, interceptores Retrofit y definición de WorkManager.
- Tablas Room de lecturas, sesiones, outbox, fotos, versiones y migraciones.

**Backend**
- `MobileReadingTraceValidator.java` (incluidos los lugares equivalentes a líneas 26/51 del log anterior).
- `LecturasApi.uploadLecturasMobile()` (equivalente a línea 537 del log anterior).
- `TrackingController`, servicio de tracking, DTO de `/api/tracking/start`, persistencia y restricciones de unicidad.
- Procesamiento transaccional del upload de lecturas, idempotencia y respuestas parciales.
- PostgreSQL: columnas e índices de sesiones, lecturas, puntos y operaciones.

### 2.2 Entregable de auditoría

Crear `docs/sync/AUDITORIA_SINCRONIZACION.md` con:
- Diagrama de secuencia **real** de captura → outbox → start/reconciliación → upload → confirmación.
- Contratos JSON reales y códigos HTTP de cada endpoint relevante, con datos ficticios.
- Inventario de estados locales, concurrencia, reintentos y garantías transaccionales.
- Identificación exacta de dónde se genera UUID local, dónde se recibe remoto y cuál se envía al backend.
- Explicación comprobable de por qué el backend rechaza las lecturas y por qué se repite `/api/tracking/start`.
- Riesgos de compatibilidad y pruebas existentes.

### 2.3 Reproducción mínima

Reproducir en entorno de desarrollo: (a) lectura offline con sesión local; (b) retorno de conectividad; (c) fallo o timeout de `startSession`; (d) subida de lectura; (e) reintento de la misma operación. Registrar IDs de correlación sin exponer ubicaciones reales. Crear test de regresión **antes** de corregir.

**Criterio de salida:** existe causa raíz sustentada en código + prueba; si no, presentar hipótesis alternativas y evidencia faltante, sin cambios especulativos.

## 3. Fase 2 — Contrato y reconciliación de sesión GPS

### 3.1 Modelo lógico recomendado (adaptar al esquema existente)

```kotlin
@Entity(tableName = "gps_session_sync")
data class GpsSessionSyncEntity(
    @PrimaryKey val localSessionUuid: String,
    val remoteSessionUuid: String?,
    val userId: Long,
    val sessionDate: String,
    val status: String, // LOCAL_ONLY, REGISTERING, CONFIRMED, NEEDS_REPAIR
    val lastErrorCode: String? = null
)
```

No crear esta tabla si ya existe una representación equivalente. Añadir migración Room **no destructiva** y pruebas de migración si se requieren columnas nuevas. Si hay múltiples dispositivos/usuarios/emisiones, asegurar que la identidad de sesión no se confunda entre ellos.

### 3.2 Algoritmo requerido

1. Al capturar, asociar lectura con **identidad local estable de sesión** y metadatos de captura; guardar lectura + outbox en una transacción Room.
2. Antes del upload, resolver `remoteSessionUuid` desde almacenamiento persistente.
3. Si no existe, invocar `/api/tracking/start` o mecanismo existente de consulta/recuperación. **Inspeccionar contrato**: determinar si recibe `clientSessionUuid` o clave idempotente y si devuelve UUID remoto.
4. Si el backend confirma sesión existente, recuperar el mismo UUID remoto; si crea una nueva, guardar su UUID de manera transaccional.
5. Construir el payload de lectura con UUID remoto **solo donde el contrato backend lo requiera**; no sobrescribir UUID local de auditoría.
6. Si no se pudo confirmar UUID remoto, dejar lectura en estado pendiente con error recuperable de dependencia; **no enviarla repetidamente sabiendo que fallará con 400**. Las demás lecturas independientes pueden continuar.
7. Cuando la fecha real de captura requiera otra jornada, resolver la sesión remota de esa fecha sin alterar falsamente la fecha de captura.
8. `TrackingSyncWorker` podrá transmitir puntos/gaps/desviaciones después, sin afectar el estado confirmado de las lecturas.

**Backend:** hacer idempotente el alta/recuperación de sesión mediante clave de cliente estable y ámbito apropiado (usuario/dispositivo/jornada/emisión según reglas reales). Definir unicidad en DB y controlar carreras concurrentes. No inferir que `/api/tracking/start` ya admite esto: verificar y ampliar de manera compatible si hace falta.

**Política de error:** un `400` de sesión inválida debe conducir a una única reconciliación controlada cuando sea seguro; si persiste, `NEEDS_REPAIR` con diagnóstico, sin bucle infinito ni pérdida de lectura.

## 4. Fase 3 — Outbox robusto e idempotencia de lecturas

### 4.1 Estados sugeridos

`PENDING` → `IN_FLIGHT` → `SYNCED`  
`IN_FLIGHT` → `RETRY_WAIT` (red/5xx/429)  
`IN_FLIGHT` → `NEEDS_REPAIR` (datos inválidos o dependencia sin resolver)

Persistir `operationId` estable (UUID generado una vez), `attemptCount`, `nextAttemptAt`, `lastHttpStatus`, `lastErrorCode`, `createdAt`, `updatedAt`, y un estado recuperable ante cierre abrupto. Respetar estados actuales si equivalentes.

### 4.2 Backend idempotente

- Inspeccionar si ya existe deduplicación por ID de lectura/emisión o identificador de operación.
- Si es insuficiente, incorporar clave idempotente persistida y **restricción UNIQUE** con ámbito correcto; no depender solo de caché en memoria.
- El reenvío de la misma operación debe devolver el resultado ya confirmado, sin duplicar lectura, historial, foto o efectos secundarios.
- Una misma clave con payload diferente debe producir conflicto explícito y auditable.
- Aplicar la deduplicación dentro de la misma transacción que los cambios de negocio.

**Ejemplo conceptual, no contrato vigente:**

```json
{
  "batchId": "b81fcb3d-05ce-4b74-9f23-6a1b2fd3e01e",
  "lecturas": [
    {
      "operationId": "7a3b8c2d-4c12-4b78-9f13-18a94f2a6631",
      "idLectura": 3025496,
      "trackingSessionId": "UUID_REMOTO_CONFIRMADO",
      "valorLectura": 1458
    }
  ]
}
```

**No reemplazar automáticamente el DTO actual** por este ejemplo; implementar campos opcionales/versionados si se aprueba.

### 4.3 Respuestas y fallos

- `2xx`: confirmar **solo** las operaciones inequívocamente aceptadas por backend.
- `400` de validación: clasificar por código de negocio; reconciliar si procede o `NEEDS_REPAIR`.
- `401`: renovar credenciales mediante mecanismo existente; evitar bucle.
- `409`: determinar si es operación ya procesada o conflicto real; nunca asumir éxito.
- `429`: respetar `Retry-After` cuando exista.
- `5xx`, timeout, red caída: backoff exponencial con jitter y límites.
- Respuesta parcial: persistir estado por operación; no marcar lote entero como éxito.
- Proceso interrumpido: recuperar `IN_FLIGHT` expirados mediante lease/timeout seguro.

## 5. Fase 4 — Orquestación y concurrencia Android

1. Identificar todos los disparadores: botón manual, conectividad, inicio de jornada, sincronización inteligente, workers periódicos, subida de foto y tracking.
2. Definir **un coordinador** de sincronización de lecturas por usuario/emisión/dispositivo; usar `WorkManager.enqueueUniqueWork` con política apropiada y exclusión adicional cuando distintas rutas invocan directamente repositorios.
3. Evitar `startSession` por cada lectura: cachear resolución confirmada en Room y sincronizar una sola creación/recuperación por sesión, incluso bajo concurrencia.
4. Asegurar orden por dependencia: **sesión remota confirmada → lecturas → fotos asociadas**; tracking de puntos en carril independiente.
5. Mantener prioridades sin hambre: lecturas primero, fotos después, clientes/geo y downloads según necesidad; evitar que tracking consuma toda la red.
6. Probar cambios de usuario, logout/login, dispositivo compartido, múltiples rutas, jornada cruzando medianoche y conectividad intermitente.
7. Si existe WebSocket, utilizarlo para notificaciones/estado; el acuse definitivo de escritura debe venir del endpoint HTTP y de persistencia en backend.

## 6. Fase 5 — Lotes, fotografías y GPS

### Lecturas
- Medir tamaño de payload, tiempo, fallos y latencia; iniciar pruebas con lotes de **hasta 50 lecturas** como hipótesis, no imponer el número sin verificar límite del servidor y contrato actual.
- Confirmación por elemento cuando el backend soporte resultados parciales; si es todo-o-nada, documentar y conservar consistencia.
- Aplicar timeouts y concurrencia limitados; evitar reenviar todo el outbox por un único registro inválido.

### Fotos
- No enviar foto hasta confirmar la lectura/ID remoto que referencia.
- Mantener marca de agua existente; preservar original o respaldo conforme política vigente.
- Considerar checksum e idempotencia para evitar duplicación tras timeout.
- No eliminar archivo local por mero HTTP exitoso si la política exige conservación.

### Tracking
- Conservar lotes de hasta 50 puntos según implementación actual; comprobar si se envían lotes de 1 sin necesidad.
- Asegurar identificador estable por punto, orden lógico, deduplicación y acuses por lote.
- Manejar `finish` solo cuando la sesión y sus dependencias estén coherentes; especificar política para puntos tardíos.
- Nunca inventar puntos ni cambiar precisión/fecha para superar validaciones.

## 7. Fase 6 — Download seguro y evolución incremental

**Primero:** verificar que `GET /api/mobile/sync/all-assigned` y `/api/lecturas/by-rutas` realizan merge sin pisar lecturas, fotos o clientes pendientes. Agregar tests con operaciones `PENDING`, `IN_FLIGHT`, `RETRY_WAIT`, `NEEDS_REPAIR`.

**Después, como mejora opcional y compatible:** diseñar `GET /api/mobile/sync/changes?cursor=...` o mecanismo equivalente, con cursor opaco/versionado, paginación, altas, cambios y eliminaciones lógicas. El cursor debe avanzar **solo después** de guardar todas las páginas de manera segura en Room. No introducir sincronización incremental si el backend no puede garantizar orden estable y consistencia.

Conservar descarga completa como recuperación/reinstalación; no reemplazarla sin migración y pruebas.

## 8. Fase 7 — Observabilidad, auditoría y operación

Instrumentar métricas **sin exponer datos personales o coordenadas**:
- Lecturas pendientes, sincronizadas, rechazadas, en reparación y edad de la más antigua.
- Duración p50/p95 de upload/download; bytes y tamaño medio por lote.
- Reintentos por HTTP y código de negocio; ratio de éxito por versión de app.
- Cantidad de altas de sesión GPS, reconciliaciones y conflictos UUID local/remoto.
- Puntos GPS pendientes y antigüedad; fotos pendientes y fallidas.
- `correlationId`, `operationId`, `batchId`, `localSessionUuid`/referencia remota **solo si la política de privacidad lo permite**, con protección apropiada.

Agregar pantalla de diagnóstico para soporte: último sync exitoso, cantidades pendientes, errores reparables y acción manual **sin borrar cola**.

## 9. Pruebas mínimas obligatorias

| Caso | Resultado esperado |
|---|---|
| Lectura offline y conexión posterior | Una sola lectura confirmada; outbox queda `SYNCED` |
| UUID local diferente al remoto | Se resuelve y usa UUID remoto válido |
| `startSession` responde timeout tras crear sesión | Reintento idempotente recupera la misma sesión |
| Backend rechaza sesión inválida | No hay reintento infinito; registro queda reparable |
| Dos workers arrancan a la vez | No duplican sesión ni lectura |
| Timeout de upload tras commit backend | Reintento no duplica efectos |
| Lote con una lectura inválida | Las demás no se pierden; resultado coherente con semántica real |
| App se cierra durante upload | Se recupera estado sin pérdida ni falso éxito |
| Foto falla después de lectura exitosa | Lectura sigue confirmada; foto permanece pendiente |
| Tracking falla o tiene 1.000 puntos pendientes | No bloquea lectura con sesión ya confirmada |
| Download durante cambios locales pendientes | Merge no sobrescribe trabajo del operador |
| Reinstalación/upgrade de Room | Migración conserva outbox y sesiones |
| Cambio de usuario/emisión/ruta | Sin mezcla de datos o sesiones |
| Jornada con fecha distinta a captura | Reconciliación correcta, sin falsear timestamps |
| GPS ausente y fallback | Fuente explícita, validación conforme reglas autorizadas |
| Reintentos 401/429/5xx | Política de backoff y clasificación correcta |

**Herramientas según proyecto:** tests unitarios Kotlin/JUnit, pruebas Room de migración, MockWebServer/Retrofit, tests de integración Spring Boot + PostgreSQL (preferible Testcontainers si está disponible), y ensayo en dispositivo con red intermitente. No afirmar que una prueba pasó si no se ejecutó.

## 10. Criterios de aceptación / Definition of Done

- [ ] La causa del error `La sesion GPS debe usar su UUID remoto` está documentada y cubierta por regresión.
- [ ] Todas las lecturas enviadas con sesión GPS requerida referencian UUID remoto confirmado.
- [ ] Fallo de upload de puntos GPS no bloquea lecturas cuya sesión ya fue confirmada.
- [ ] Reintentos no crean sesiones, lecturas o fotos duplicadas.
- [ ] No hay pérdida de outbox en reinicio, caída de red, timeout ni migración.
- [ ] No existen bucles infinitos por errores 400 permanentes.
- [ ] Se evita concurrencia accidental de uploads de la misma cola.
- [ ] Download preserva datos locales pendientes.
- [ ] Se mantienen permisos, autenticación, reglas de negocio y auditoría existentes.
- [ ] Tests automatizados relevantes pasan y se documentan comandos/resultados.
- [ ] Se documentan migraciones, compatibilidad y rollback.
- [ ] Existe evidencia de prueba end-to-end con conectividad inestable.

## 11. Estrategia de despliegue y rollback

1. Separar cambios en PRs pequeños: **(A) diagnóstico/tests**, **(B) sesión GPS**, **(C) outbox/idempotencia**, **(D) concurrencia**, **(E) descargas/métricas**.
2. Para cambios de contrato, desplegar primero backend compatible con clientes antiguos; después Android; finalmente retirar compatibilidad solo con evidencia de adopción y aprobación.
3. Ejecutar migraciones DB expansivas y reversibles cuando sea viable; crear respaldo previo.
4. Habilitar por feature flag si el proyecto dispone de mecanismo; probar con lectores piloto.
5. Monitorear rechazos 400, duplicados, backlog y duración de sincronización.
6. Rollback de código sin borrar columnas, outbox ni registros; no revertir una migración que destruya datos.

## 12. Entregables que el agente debe producir

1. `docs/sync/AUDITORIA_SINCRONIZACION.md`: arquitectura real, causa raíz, evidencia y contratos.
2. `docs/sync/CONTRATOS_Y_ESTADOS.md`: diagramas de estados, secuencia y DTO antes/después.
3. Código Android/backend modificado con migraciones y tests.
4. `docs/sync/PRUEBAS_Y_RESULTADOS.md`: casos ejecutados, comandos, resultados y pendientes.
5. `docs/sync/DEPLOY_ROLLBACK.md`: orden de despliegue, compatibilidad, monitoreo y recuperación.
6. Resumen final: archivos modificados, riesgos, decisiones que requieren aprobación y métricas comparativas antes/después (si se midieron).

## 13. Prompt listo para pegar al agente

> Revisa este documento y el repositorio completo de EpmpatMobile y ERP EPMAPA-T. Ejecuta las fases en orden, empezando por una auditoría de solo lectura. Identifica y reproduce la causa raíz de `400 La sesion GPS debe usar su UUID remoto`. No presupongas contratos ni estructuras: verifica clases, endpoints, DTO y tablas existentes. Presenta el diagnóstico y un plan de cambios con riesgos antes de introducir modificaciones de contratos o migraciones. Implementa primero la reconciliación idempotente de UUID local/remoto, luego outbox, concurrencia, lotes y descargas, preservando Offline-First y trazabilidad GPS. Mantén puntos GPS asíncronos, pero no envíes lecturas con sesión remota no confirmada si el backend la exige. Añade pruebas de regresión, integración, migraciones no destructivas, métricas y documentación de despliegue/rollback. No desactives validaciones, no inventes datos GPS, no borres registros pendientes y no marques operaciones como sincronizadas sin confirmación. Al finalizar cada fase informa archivos cambiados, pruebas ejecutadas y bloqueos; detente para solicitar aprobación si necesitas modificar contratos incompatibles, políticas GPS o datos de producción.

---

**Nota de alcance:** los campos, estados y endpoint incremental propuestos son diseños sugeridos, no funcionalidades verificadas. La documentación suministrada describe los flujos, pero no incluye implementación completa ni respuestas JSON reales. El agente debe validar todo contra el código fuente antes de ejecutar.
