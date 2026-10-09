# Trazabilidad mobile y geolocalizacion obligatoria

Cambios coordinados en `EpmpatMobile` y `erp_epmapat_be`. No desplegados ni instalados en telefonos.

## Correcciones

- Las lecturas usan el UUID remoto de su sesion GPS. El historial local consulta ese UUID y conserva la consulta del ID local para registros antiguos.
- Al guardar se exige permiso de ubicacion precisa, GPS habilitado, jornada del mismo lector y dia, y una captura nueva del proveedor del telefono (maximo 30 segundos de antiguedad). Se guarda latitud, longitud, precision y fecha de captura ISO con zona explicita.
- La ubicacion de referencia del medidor se mantiene separada de la captura del lector. Se aceptan referencias con y sin corchetes y se valida el rango. El cambio de referencia se persiste antes de preparar su envio.
- Lectura, punto GPS de la captura y envio pendiente se guardan en una transaccion local. La confirmacion de un envio no puede marcar como enviada una edicion posterior del mismo registro.
- La pantalla de acceso exige permiso preciso y GPS activo, incluye botones para conceder permiso y abrir Ajustes, y revisa los cambios de permisos/GPS. Android conserva la decision sobre los permisos: la aplicacion bloquea el trabajo hasta que se cumplan los requisitos. Referencia: [permisos de ubicacion de Android](https://developer.android.com/develop/sensors-and-location/location/permissions).
- El inicio/reanudacion de jornada se ejecuta desde la aplicacion autenticada para todas sus pantallas, con exclusión mutua para evitar dos sesiones locales. Al cerrar sesion se finaliza la jornada.
- Se programan sincronizaciones GPS inmediatas y periodicas. El servicio solicita sincronizacion aproximadamente cada minuto cuando recibe puntos y recupera la sesion ante un reinicio del servicio. Al reiniciar el telefono solo se intenta iniciar GPS si existen los permisos necesarios; en caso contrario se reanuda al abrir la app.
- Antes de enviar lecturas se confirma la creacion de su sesion remota. Los workers confirman las respuestas de inicio, lote y cierre antes de marcar datos como enviados. Las llamadas de tracking y lecturas solicitan JSON explicitamente.
- El nuevo GET `/lecturas/{id}/mobile` devuelve el DTO plano para confirmar los datos guardados despues del upload por lote. El PUT mobile devuelve el mismo DTO. La app compara sesion, coordenadas, precision y fecha, ademas de los campos operativos que ya verificaba.
- El backend exige trazabilidad completa en los endpoints mobile: UUID remoto, coordenadas/precision validas, fecha, sesion existente, lector coincidente y fecha correspondiente a la jornada en America/Guayaquil. Una lectura invalida se rechaza y queda pendiente en la app.
- El servicio copia distancia/estado respecto al medidor al actualizar. La auditoria del estado anterior incluye ahora los campos GPS y de distancia.

## Verificacion automatica

Android: `gradlew.bat :app:testDebugUnitTest :app:assembleDebug`.

Pruebas del contrato GPS: UUID, coordenadas incompletas/fuera de rango, precision, fechas equivalentes con zona, discrepancias del servidor y referencias con corchetes.

Backend:

```powershell
mvn -q "-Dtest=MobileReadingTraceValidatorTest,LecturaTracePersistenceTest,LecturasMobileContractTest,MobileWebSocketHandlerTest" test
```

Comprueban validacion de sesion/lector/fecha, rechazo de datos incompletos, persistencia y auditoria GPS, DTO JSON plano de confirmacion y aislamiento de fallos WebSocket.

APK de prueba: `../EpmpatMobile/app/build/outputs/apk/debug/app-debug.apk`. Es un APK debug; la actualizacion de telefonos productivos debe conservar la firma oficial y los datos locales. No desinstalar la app para sortear una incompatibilidad de firma: podria eliminar pendientes.

## Activacion y prueba real pendiente

Actualizar backend y app de forma coordinada: el APK necesita el nuevo endpoint de confirmacion y el backend rechaza la trazabilidad incompleta de APK antiguos. Verificar previamente las tablas/campos de tracking existentes y planificar la actualizacion de los telefonos durante la activacion.

1. Entrar sin permiso, con permiso aproximado y con GPS apagado: debe aparecer el bloqueo. Conceder ubicacion precisa y activar GPS: debe permitir continuar e iniciar/reanudar la jornada.
2. Capturar una lectura con foto sin conexion: debe quedar pendiente con UUID remoto, coordenadas nuevas, precision y fecha, mas un punto GPS local.
3. Recuperar conexion: comprobar sesion creada, puntos enviados y confirmacion de la lectura/foto. Verificar `GET /tracking/sessions` y `GET /tracking/sessions/{uuid}/full-trace` en el backend utilizado por el telefono.
4. Ejecutar `src/main/resources/sql/2026-10-08_verificar_trazabilidad_mobile.sql` ajustando el periodo. Para las nuevas lecturas de prueba no deben existir sesiones faltantes ni coordenadas/precision incompletas; debe haber puntos para su sesion y coincidencia del lector.
5. Revocar el permiso o apagar GPS durante el uso: el trabajo debe quedar bloqueado. Restaurarlos, volver y comprobar reanudacion y captura nueva.
6. Cerrar y volver a abrir la app, finalizar jornada y probar cambio de dia: comprobar recuperacion y sincronizacion de pendientes. En un telefono real, validar tambien pantalla apagada y restricciones de bateria del fabricante.
7. Editar una lectura mientras se sincroniza una version anterior: la nueva version debe permanecer pendiente hasta su propia confirmacion.

No habia dispositivos ADB conectados durante la revision. No se realizo captura GPS real, instalacion, despliegue ni modificacion de datos productivos. La verificacion automatica no acredita que los APK actualmente instalados incluyan estos cambios ni que la produccion ya este recibiendo trazas nuevas.

## Datos historicos

El ID local antiguo se traduce al UUID solo cuando existe la sesion local que permite identificarlo sin ambiguedad. Los registros sin coordenadas o precision no se completan con ubicaciones inventadas y permanecen con error de trazabilidad para revision. No se debe recapturar una ubicacion actual y presentarla como evidencia de una visita historica; una correccion debe identificarse como una nueva captura.

Los contadores GPS no demuestran por si solos que todas las lecturas/fotos esten confirmadas. Revisar los pendientes y los datos persistidos, ademas del recorrido.
