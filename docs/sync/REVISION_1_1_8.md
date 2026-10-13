# Revision 1.1.8 (versionCode 10)

Se corrigio el reintento manual: valida payload y operationId en una transaccion Room antes de restablecer la sesion. Una operacion anterior con los mismos valores no puede restablecer una edicion nueva; un cambio concurrente produce un aviso para actualizar la lista.

Se corrigio la actualizacion GPS parcial en backend: conserva todos los campos GPS previos cuando la entrada no contiene trazabilidad completa. No afirma GPS_VALID para una actualizacion generica sin validar; usa GPS_UNKNOWN. La via mobile validada con datos completos puede marcar GPS_VALID. La confirmacion autorizada sin coordenadas sigue marcando GPS_PENDING y conserva la ubicacion anterior.

No hay nuevas columnas ni cambio de Room: esquema 19. Siguen siendo requisitos los tres scripts SQL anteriores: mobile_sync_receipts, historical_tracking_days y reading_gps_pending. Aplicarlos antes del WAR si aun no se completaron.

Artifacts: backend target/sync-review/erp_epmapat-v0.1.war; mobile app/build/outputs/apk/release/EpmpatMobile-1.1.8-piloto-debug-signed.apk, firmado con la misma clave debug de esta computadora. Actualizar sobre la app existente sin desinstalar ni borrar almacenamiento.

Android: 16 pruebas JVM y 13 instrumentadas correctas, incluidas migracion desde v12, envio sin GPS y proteccion de operaciones frente a acuses/reintentos antiguos. Backend: evidencia actual en target/sync-review/review-tests.log y surefire-reports. Se reejecutaron pruebas PostgreSQL con Docker local tras detectar omision en la primera pasada.

No se accedio a las 424 capturas reales del telefono ni se modifico produccion. El piloto debe comprobar lectura confirmada, GPS_PENDING/GPS_VALID/GPS_UNKNOWN y conservacion de fotos, puntos y fecha original. Una lectura confirmada con GPS_PENDING no tiene trazabilidad GPS completa.
