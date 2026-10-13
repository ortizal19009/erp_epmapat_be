# Revision previa al despliegue

## Correccion adicional

La normalizacion de la captura ahora compara operationId ademas del payload. Una nueva operacion con valores iguales no puede ser modificada por el intento anterior. SyncIntegrityTest comprueba que el intento antiguo no normaliza, confirma ni marca SENT la nueva operacion.

## Pendientes de distribucion

El usuario confirma que el celular utiliza HTTP. El release existente bloquea HTTP mediante usesCleartextTraffic=false; no distribuir ese APK hasta configurar una excepcion para el host real y comprobar su manifest. Se solicito la direccion completa para limitar la excepcion al servidor utilizado.

Falta la firma original (.jks/.keystore) y el destino de despliegue. No reemplazar la app por un debug firmado con otro certificado ni desinstalarla. No compartir passwords en el chat.

El backend tiene ddl-auto=none: copiar el WAR no ejecuta el SQL. Aplicar primero el script expansivo con respaldo de PostgreSQL. deploy.sh respalda el WAR anterior, pero no respalda la base ni aplica migraciones SQL ni comprueba salud; seguir DEPLOY_ROLLBACK.md antes de ejecutar ese script.

No hay cambios adicionales necesarios en frontend para estos contratos opcionales. El piloto fisico y la reparacion de sesiones ambiguas siguen pendientes.

## Datos confirmados por el usuario

Servidor: http://192.168.0.165:8080. GET /usuarios/ping respondio HTTP 200, cuerpo OK, desde esta computadora. Se agrego configuracion de seguridad de red exclusiva de release: HTTP permitido solo para 192.168.0.165; el resto conserva bloqueo. No se cambiaron preferencias almacenadas del celular.

La instalacion proviene de Android Studio o app-debug.apk. Existe C:/Users/Alexi/.android/debug.keystore. Se prepara un APK release firmado con esa clave de desarrollo, para piloto compatible si la instalacion original usa el mismo certificado. No es una firma privada de distribucion release. No generar otra clave ni sustituir la existente. Si Android rechaza la firma, conservar la instalacion y revisar el certificado del APK original.

Antes de desplegar falta acceso al host de ejecucion del backend y a PostgreSQL para respaldo y SQL. La URL HTTP no identifica por si sola usuario SSH, directorio remoto ni ubicacion de la base. No se ejecuto deploy.sh ni se altero el servidor.

## Verificacion final

Reejecucion: BUILD SUCCESSFUL; 16 pruebas JVM y 7 instrumentadas sin fallos. El manifest empaquetado release referencia network_security_config. APK piloto: EpmpatMobile/app/build/outputs/apk/release/EpmpatMobile-1.1.4-piloto-debug-signed.apk. apksigner verify confirma firmas v2 y v3; certificado coincide con el app-debug.apk generado en esta computadora.

Certificado SHA-256: 4ee223dccd2d8e9972afc7b1286af44df4835431d9e8b05ad58bec6add47ffed.
APK SHA-256: 9F25C374FDD8493EA4F3A8DF9C57D49E8E6552E6F77A52B1F6A66D89E2338D2C.

No se conecto el celular fisico; su firma instalada aun debe coincidir. El backend y SQL deben desplegarse primero. El emulador temporal se cerro despues de las pruebas.

## Diagnostico de sincronizacion inteligente

Se reviso el boton del dashboard: no vuelve a enviar registros NEEDS_REPAIR ni AUTH_REQUIRED. Antes terminaba con un mensaje generico de bandeja pendiente. Ahora muestra tipo, registro, estado, detalle guardado y accion recomendada; mantiene la descarga pausada y conserva la captura. Los errores locales controlados de GPS/sesion se guardan con su detalle en lugar de ocultarse como VALIDATION_OR_CONFLICT. Para actualizar un diagnostico antiguo, usar Reintentar en el registro de Cambios pendientes. No se repara automaticamente una jornada ambigua.

## Correccion del envio de lecturas - version 1.1.5 (versionCode 7)

El boton manual ahora reevalua una vez las lecturas propias NEEDS_REPAIR con el diagnostico antiguo DEPENDENCY_FAILED/VALIDATION_OR_CONFLICT. Conserva operationId, payload y confirmacion de datos/foto. Si vuelve a fallar, guarda DEPENDENCY_VALIDATION con el detalle actual; no se crea un bucle de reintentos ni se habilitan errores definitivos nuevos indiscriminadamente.

ReadingSessionResolver puede encontrar la sesion original por un punto exacto cuando la referencia local falta, apunta a otro dia o no puede resolverse. Busca puntos enviados y pendientes con el mismo instante, coordenadas, precision, lector y jornada. Solo una coincidencia es recuperable. No reconstruye fecha ni GPS. Las lecturas se envian independientemente de los puntos pendientes despues del acuse de sesion.

Pruebas: 16 JVM y 11 instrumentadas correctas. Cuatro pruebas nuevas ejecutan el repositorio real de upload con Room real y APIs simuladas: lectura enviada con 1000 puntos pendientes, referencia ausente recuperada por punto ya enviado, antiguo NEEDS_REPAIR confirmado sin cambiar operationId y fecha incompatible conservada tras una unica reevaluacion. El instrumento se ejecuto explicitamente en emulator-5556, un AVD independiente temporal; no se cambio el emulator-5554 existente ni un telefono fisico.

APK vigente: EpmpatMobile/app/build/outputs/apk/release/EpmpatMobile-1.1.5-piloto-debug-signed.apk. Firma debug de esta computadora verificada. Instalar como actualizacion, sin desinstalar. No se requieren cambios adicionales de backend para esta correccion; el esquema completo y backend anterior siguen siendo requisitos. Los 424 datos reales del celular aun no han sido inspeccionados: la correccion permite enviar los recuperables y conserva los casos sin evidencia suficiente.
