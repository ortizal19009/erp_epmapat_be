# Fotos pendientes sin bloqueo operativo - 1.1.9 (versionCode 11)

## Cambio

La sincronizacion inteligente solo pausa la descarga cuando quedan datos sin confirmar. Una LECTURA con dataConfirmed=1 y status distinto de SENT conserva su foto pendiente pero permite continuar la descarga y las otras lecturas. Clientes, medidores y lecturas sin acuse siguen bloqueando la descarga. Las descargas usan merge y protegen las lecturas con fotos pendientes; no reemplazan la foto local ni su captura.

No se marcan como SENT fotos rechazadas. El aviso final indica cuantas fotos se conservan; la cola puede seguir mostrando 96 pendientes aunque la sincronizacion de datos avance. Los workers mantienen ese aviso en lugar de declarar todo completado.

HTTP 401 sigue pausando por autenticacion. HTTP 403 ahora pide revisar permisos (NEEDS_REPAIR/PERMISSION_DENIED); no afirma que otro login resuelva la causa. El fallo de foto muestra PHOTO_UPLOAD_FORBIDDEN y origen del servidor de la respuesta sin incluir token, password, query o contenido del archivo.

No se soluciono aun el rechazo 403 de las fotos reales. Nextcloud health del backend confirmo acceso de lectura con la cuenta configurada; eso no demuestra permiso de escritura. Una peticion diagnostica al endpoint omitiendo el archivo obligatorio respondio 400, sin crear ni cambiar datos. No se atribuye por eso el rechazo real a Nextcloud o al proxy.

## Actualizacion

Instalar app/build/outputs/apk/release/EpmpatMobile-1.1.9-piloto-debug-signed.apk sobre la instalacion existente, sin desinstalar ni borrar datos. Firma debug de esta computadora. No requiere nuevo SQL ni otro WAR respecto al backend compatible anterior. Room permanece en 19.

Pulsar Sincronizacion inteligente. Si quedan solo fotos confirmadas en datos, deben descargarse asignaciones sin eliminar evidencias. En Cambios pendientes reintentar 3035957 una vez y revisar su nuevo destino HTTP si persiste el 403. La foto debe permanecer pendiente; no debe repetirse la escritura de la lectura.

## Validacion

Prueba Room: lectura confirmada con foto AUTH_REQUIRED no bloquea datos; merge mantiene valor de lectura, foto y outbox; agregar otra lectura sin confirmar vuelve a bloquear. Prueba de politica: 403 pide reparar permisos y 401 pide autenticacion. Evidencia en app/build/sync-review/photo-pending-build.log y photo-pending-device-tests.log. Piloto fisico pendiente; no se inspeccionaron ni borraron las 96 fotos del celular.
