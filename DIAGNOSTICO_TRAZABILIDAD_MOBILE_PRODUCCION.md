# Trazabilidad móvil en producción — 2026-10-08

## Resultado

**La trazabilidad está guardándose parcialmente.** Se encontraron auditorías de modificaciones móviles y lecturas persistidas, pero no un recorrido GPS reciente ni una relación válida entre las lecturas recientes y las sesiones.

Se consultó exclusivamente en modo de solo lectura `192.168.0.46:5432/ErpEpmapaT`. El backend comprobado es `192.168.0.165:8080`. No se enviaron lecturas, puntos GPS, correos ni modificaciones a producción. La revisión se centró en los datos del 1 al 8 de octubre, sin seleccionar coordenadas, identificadores de dispositivos ni datos personales.

## Evidencia de base de datos

| Evidencia | Resultado |
|---|---:|
| Sesiones GPS con jornada del 1 al 8 de octubre | 0 |
| Puntos GPS con captura del 1 al 8 de octubre | 0 |
| Sesiones GPS existentes en toda la tabla | 5 |
| Puntos GPS existentes en toda la tabla | 16.563 |
| Última fecha de jornada GPS | 2026-09-22 |
| Última captura de punto GPS | 2026-09-22 08:42:57 |
| Última inserción de punto GPS (`created_at`) | 2026-09-22 10:10:59.965724 |
| Lecturas con captura del 5 al 7 de octubre | 3.673 |
| De esas lecturas, con identificador de sesión no nulo | 3.673 |
| De esas lecturas, con latitud y longitud | **0** |
| De esas lecturas, vinculadas a una sesión existente | **0** |
| De esas lecturas, vinculadas a puntos GPS existentes | **0** |
| De esas lecturas, con alguna auditoría móvil del período | 3.673 |

Los 3.673 identificadores de sesión de esas lecturas tienen formato numérico, compatible con un ID local de Room, mientras que el código de sincronización de sesiones usa `remoteId`.

### Lecturas por fecha de captura persistida

| Fecha de captura | Lecturas | Con sesión indicada | Con coordenadas completas |
|---|---:|---:|---:|
| 2026-10-05 | 793 | 793 | 0 |
| 2026-10-06 | 1.033 | 1.033 | 0 |
| 2026-10-07 | 1.847 | 1.847 | 0 |

Última fecha/hora de captura de lectura encontrada: `2026-10-07 15:12:32`. No se encontraron capturas fechadas el 8 en este corte, aunque sí modificaciones y auditorías del día 8.

### Auditoría de lecturas con observación que contiene “mobile”

| Fecha persistida | Eventos de auditoría |
|---|---:|
| 2026-10-05 | 4.518 |
| 2026-10-06 | 6.980 |
| 2026-10-07 | 12.025 |
| 2026-10-08 | 583 |

Los 583 eventos del día 8 corresponden a **155 IDs de lectura distintos**, con último registro `2026-10-08 07:38:42.483`. No deben interpretarse como 583 lecturas nuevas ni como 583 capturas GPS. Puede haber varias auditorías de una misma lectura.

En `lecturas`, el día 8 hay 156 registros con `fecmodi` de esa fecha: 155 tienen sesión y fecha de captura informadas, pero ninguno tiene latitud o longitud. La cobertura de auditoría por ID demuestra que existe al menos un evento en el período; no demuestra que cada intento de envío tenga su propio registro.

Las fechas se presentan **tal como están almacenadas**. La sesión SQL usa `Etc/UTC`, pero las columnas de captura y auditoría son `timestamp without time zone`; no se asignó retrospectivamente una zona horaria a sus valores. `lecturas.fecmodi` es `date` y no permite reconstruir la hora del guardado.

## Evidencia del backend desplegado

Se realizaron GET sin modificar datos:

- `http://192.168.0.165:8080/tracking/sessions?date=2026-10-08`: HTTP 200, lista vacía.
- `http://192.168.0.165:8080/tracking/sessions?date=2026-09-22`: HTTP 200, una sesión.
- `/api/tracking/sessions?date=2026-10-08`: HTTP 404. Para este backend la ruta comprobada de tracking está directamente bajo `/tracking`; hay que verificar la URL que usa cada celular antes de atribuir el fallo a este prefijo.

Esto demuestra que la consulta de sesiones del backend responde actualmente. No acredita la disponibilidad que tuvo durante el incidente `NoRouteToHostException`, ni verifica un nuevo envío completo desde un teléfono.

## Revisión del flujo en el código local

### Lectura móvil → backend → base de datos

`LecturasSyncRepository` arma la lectura y envía `trackingSessionId`, latitud, longitud y fecha de captura. `LecturasApi.buildLecturaFromMobileItem` recibe estos campos y `LecturaServicio` los copia a `lecturas`.

El guardado con auditoría registra el **estado anterior** mediante `LecturasAuditDTO`, luego modifica la lectura. El DTO de auditoría no incluye `trackingSessionId`, coordenadas, precisión ni fecha de captura GPS. Por tanto, la auditoría actual de lectura no basta para reconstruir los cambios de ubicación.

También existen dos límites relevantes:

- En el upload por lote, si falta el parámetro `usumodi`, el controller toma la rama `saveLectura`, sin esa auditoría genérica.
- `AuditoriaGenericaService` persiste la auditoría en una transacción `REQUIRES_NEW` y tolera errores después de intentar un resumen. Una auditoría puede sobrevivir a un fallo posterior del guardado; un guardado puede continuar aunque no se haya podido guardar la auditoría. Su existencia por sí sola **no es una confirmación atómica de éxito**.

### Inconsistencia de identificadores

En el código local de Android:

```kotlin
// LecturasSyncRepository.saveLecturaLocalPending
trackingSessionId = activeSession?.id?.toString()
```

En cambio, `TrackingSyncWorker` envía la sesión con:

```kotlin
// TrackingSessionEntity.toDto
id = remoteId
```

Los puntos usan asimismo el identificador remoto de la sesión. Esta diferencia explica una posible ruptura del vínculo lectura ↔ sesión y coincide con el formato numérico observado en producción. **No explica por sí sola que no haya nuevas sesiones/puntos desde septiembre**: esa ausencia requiere revisar la sincronización GPS y el APK instalado.

### Coordenadas y sincronización GPS

La lectura obtiene coordenadas de `geolocalizacionReferencia`; si está ausente o no se interpreta como dos números, quedan nulas. El worker GPS usa un flujo independiente para iniciar sesión, enviar lotes y finalizarla. Que se suban lecturas no demuestra que el worker GPS esté funcionando.

Sin consultar la base local del celular o su Logcat no puede determinarse si las coordenadas no se capturaron, quedaron pendientes localmente, fueron omitidas por el APK instalado o se perdieron en otra revisión del backend. La inspección del código local tampoco demuestra qué versión exacta está instalada en cada celular.

### Auditoría TXT

Existe `POST /mobile/auditoria`, cuyo servicio escribe archivos `audit_YYYY-MM-DD.txt`, por defecto en Linux bajo `/var/log/epmapat/auditoria`. Es un registro **en archivos**, separado de `auditoria_generica`.

No se verificó la existencia de esos archivos en el servidor. El Compose local inspeccionado tampoco declara un volumen para esa carpeta; si esa configuración es la desplegada, habría que comprobar la persistencia del directorio antes de recrear el contenedor. No se ha confirmado la configuración efectiva de producción.

## Verificaciones pendientes para completar la cadena

1. Confirmar usuario/jornada, versión del APK y URL base configurada en el teléfono.
2. Revisar en el teléfono las sesiones/puntos pendientes de Room y los errores de `TrackingSyncWorker`: respuesta a `/tracking/start`, envío de `/tracking/points/batch` y cierre `/tracking/finish`.
3. Verificar permisos de ubicación, GPS, captura real de coordenadas y restricciones de ejecución en segundo plano.
4. Contrastar logs del backend y auditoría TXT con una operación del mismo teléfono. No se dispone de acceso autenticado a esos archivos en esta revisión.
5. Corregir y probar el uso de `remoteId` en la relación de lectura con sesión; no reasignar automáticamente IDs locales históricos, pues pueden repetirse entre celulares.
6. Evaluar una auditoría que registre origen, intento/correlación, resultado y datos GPS antes/después, además de una política explícita para fallos de auditoría.

No se modificó código funcional ni datos como parte de esta revisión. No se ejecutó una prueba nueva de escritura en producción. La afirmación sustentada es: **lecturas y auditorías llegan; el recorrido GPS y su correlación con lecturas recientes no están completos en la base consultada**.
