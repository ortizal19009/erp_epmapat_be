# Fotos mobile y Nextcloud - revision del 15 de octubre de 2026

Version mobile 1.1.10, versionCode 12. Backend conserva version de WAR v0.1. Room permanece en 19; no hay nueva migracion SQL para estos cambios.

## Resultado de accesos reales

El backend http://192.168.0.165:8080 respondio al ping y su health de almacenamiento confirmo Nextcloud en https://192.168.0.52, usuario configurado admin, carpeta /EPMAPAT-PHOTOS/. No se cambiaron credenciales ni permisos.

Se subio una imagen PNG minima en una carpeta aleatoria sync-diagnostics mediante el endpoint generico del backend; HTTP 200. Se descargo con HTTP 200 y bytes identicos. Se eliminaron solamente ese archivo y su subcarpeta de prueba, ambos HTTP 200. Evidencia sin secretos: target/sync-review/nextcloud-probe-2026-10-15.json. La prueba demuestra escritura en esa carpeta, no en cada carpeta historica de ruta.

GET /lecturas/3035957/mobile respondio HTTP 200 con referencia de foto. GET /lecturas/3035957/foto respondio HTTP 200 image/jpeg. No se reemplazo esa foto: no sabemos si coincide con la pendiente del celular.

## Endpoints comprobados

| Endpoint | Uso y confirmacion |
| --- | --- |
| POST /lecturas/mobile/upload | Datos de lectura y operationId; dataConfirmed antes de la foto |
| POST /lecturas/{id}/foto | Multipart file, query usumodi, operationId, tipo, observacion; devuelve idlectura, fotoPath y photoOperationId |
| GET /lecturas/{id}/foto | Recupera la imagen guardada por el backend |
| GET /api/storage/health | Comprueba existencia/acceso de lectura; no prueba escritura |
| POST /api/storage/upload | Subida generica usada exclusivamente para la prueba aislada; no reemplaza el contrato de foto de lectura |
| GET /api/storage/file?path=... | Lectura de archivo por el backend |
| DELETE /api/storage?path=... | Se utilizo solo para limpiar los objetos propios de la prueba |

La app sigue usando el token de su sesion hacia el backend. Las credenciales de Nextcloud permanecen en el servidor. Esta revision no sustituye la politica de autorizacion existente ni distribuye credenciales de almacenamiento en el APK.

## Correcciones

- Las rutas file:/ y file:// son archivos locales. Antes podian confundirse con referencias remotas y marcarse como enviadas sin upload. Las pendientes ahora se abren correctamente. No se reactivan indiscriminadamente filas historicas SENT.
- La confirmacion exige misma lectura, misma operacion y ruta remota no vacia. Un HTTP 200 con otra operacion, otro ID o ruta local no marca SENT.
- Se conserva dataConfirmed en fallos de foto. El reintento no reenvia los datos de lectura, no cambia operationId y mantiene el archivo original. La ruta local solo se reemplaza tras acuse valido y comparacion del payload/operacion vigente.
- Se conservaron los tres envios paralelos existentes y se hicieron atomicos sus contadores. La cancelacion no se transforma en un error de almacenamiento.
- Se agrego Reintentar fotos pendientes en Cambios pendientes. Reactiva solo fotos propias con datos confirmados y compara payload/operationId; omite otros usuarios, SENT y envios activos.
- El backend devuelve PHOTO_STORAGE_AUTHENTICATION_FAILED, PHOTO_STORAGE_PERMISSION_DENIED, PHOTO_STORAGE_QUOTA_EXCEEDED, PHOTO_STORAGE_CONFIGURATION_ERROR o PHOTO_STORAGE_UNAVAILABLE. Incluye source, upstreamStatus y retryable sin publicar cuerpos WebDAV ni claves. Un rechazo del almacenamiento no se presenta como login requerido del celular.
- Logs PHOTO_UPLOAD_STARTED, PHOTO_UPLOAD_CONFIRMED y PHOTO_STORAGE_FAILED permiten localizar la etapa; no registran contenido de imagen ni credenciales.
- Se normalizan separadores repetidos en las rutas Nextcloud y se codifican segmentos con espacios. Se rechazan componentes de recorrido fuera de la carpeta. Los nombres por operacion/checksum siguen siendo estables.

## Despliegue manual

1. Publicar primero backend target/sync-review/erp_epmapat-v0.1.war. Mantener los tres SQL anteriores ya aplicados; no hay SQL nuevo. Respaldar y seguir el procedimiento de despliegue habitual.
2. Actualizar un celular de piloto con EpmpatMobile/app/build/outputs/apk/release/EpmpatMobile-1.1.10-piloto-debug-signed.apk. Esta firmado con la misma clave debug de esta computadora. No desinstalar ni borrar datos. El backend anterior no devuelve photoOperationId y el APK nuevo conservaria la foto pendiente por falta de confirmacion.
3. En Cambios pendientes pulsar Reintentar fotos pendientes. Verificar primero 3035957: fotoPath remoto confirmado y estado SENT, sin modificar lecturaactual ni repetir el guardado de lectura. Las fotos rechazadas se conservan; otras lecturas y descargas pueden continuar cuando sus datos ya estan confirmados.
4. Si persiste PHOTO_UPLOAD_FORBIDDEN, anotar el Destino mostrado y correlacionar PHOTO_UPLOAD_STARTED con el horario del intento. Si aparece PHOTO_STORAGE_PERMISSION_DENIED, revisar permiso de escritura de la carpeta concreta; si aparece AUTHENTICATION_FAILED, revisar la credencial/token del servidor. No cambiar las credenciales que ya funcionaron sin esa evidencia.

## Validacion

40 pruebas backend, cero fallos/errores/omisiones, incluidas cinco PostgreSQL reales. Nuevas pruebas: contrato MVC de confirmacion por operacion, error WebDAV 403 separado de autenticacion mobile, tipos de fallos y cliente Sardine real contra un servidor HTTP local para MKCOL/PUT/GET y rutas con espacios.

18 pruebas JVM Android y 15 instrumentadas correctas. La nueva prueba usa Room y repositorio reales con APIs simuladas: foto file: recibe rechazo de permisos, luego acuse incorrecto y por ultimo confirmacion valida; mantiene archivo/datos y solo repite la foto. Incluye seleccion para reintento masivo. Prueba real en Nextcloud separada de las APIs simuladas y sin tocar datos de lecturas.

Reportes: backend target/sync-review/photos-2026-10-15-tests.log y surefire-reports; mobile app/build/sync-review/photos-2026-10-15-build.log y photos-2026-10-15-device-tests.log.

WAR SHA-256: E5F5EBFB5BAEA60B165D7DDFA6F063963F89FB13D429BE3D8F04F4111D5C3276
APK SHA-256: B8221093E7BAAFBF4CA98E663C467F90CCB3EFDA1FB807E2776C3F07356B900B

No se hizo despliegue ni se actualizo el telefono fisico. Falta comprobar las fotos originales del celular y el origen concreto del 403 anterior. La existencia de una foto remota no basta para confirmar otra pendiente sin su operacion.

## Hallazgo de autorizacion pendiente

La prueba generica POST /api/storage/upload y la limpieza DELETE /api/storage se aceptaron sin cabecera Authorization desde esta computadora. Ese acceso de escritura/borrado del backend no equivale a los permisos privados de la cuenta Nextcloud. La autenticacion efectiva de esas operaciones genericas requiere revision y restriccion por rol. No se ha cambiado esa politica en esta entrega ni se afirma que los endpoints esten endurecidos; hacerlo debe coordinarse con los consumidores web/mobile existentes. El nuevo diagnostico separa estos rechazos de la autenticacion del almacenamiento.
