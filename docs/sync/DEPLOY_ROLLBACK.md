# Actualizaci?n y rollback sin p?rdida de datos

## Orden de despliegue

1. Respaldar PostgreSQL y verificar el restore. Revisar src/main/resources/sql/2026-10-13_mobile_sync_receipts.sql con quien administra la base. Es expansivo: agrega recibos, identificadores de eventos e informaci?n de procedencia. Ejecutarlo antes del backend. Fue probado dos veces sobre PostgreSQL ef?mero, no aplicado en producci?n.
2. Publicar el WAR revisado. Mantener URL/basePath, autorizaci?n y almacenamiento configurados. Comprobar start con confirmIdentity, rechazo por identidad, upload con operationId, reenv?o id?ntico y foto con clave estable. Clientes antiguos son compatibles; no usar el nuevo mobile contra un backend que no confirma operationId.
3. Firmar versi?n 1.1.5, versionCode 7, con el mismo certificado y applicationId de la app instalada. No hay keystore.properties configurado. APK entregado es unsigned y no est? listo para distribuir.
4. Actualizar sobre la instalaci?n existente. No desinstalar, borrar almacenamiento ni renombrar epmpatmobile.db. Room migra 12?15?16?17 sin vaciar lecturas, fotos o cola. Versiones intermedias no documentadas requieren migraci?n espec?fica antes de publicar.
5. Piloto con un lector: inventario de lecturas/cola/puntos/fotos antes y despu?s; sincronizar con cortes de red; confirmar recibos y datos por ID. Revisar lecturas en NEEDS_REPAIR sin fabricar jornada o GPS. Ampliar distribuci?n despu?s de revisar el piloto.

## Lectura 3032370 / fecha diferente

Mantener captura y sesi?n originales. La app intenta vincular solo el punto original inequ?voco. Si no hay coincidencia, revisar sesi?n local, aliases, lector, dispositivo y jornada contra servidor. Un operador debe resolver la causa antes del reintento manual. No editar fechas ni borrar puntos para ocultar el rechazo.

## Observaci?n

Revisar mobile.sync.readings y m?tricas de duraci?n/lote, estados de cola y GPS bloqueado. No habilitar logging BODY para investigar: contiene ubicaciones y datos personales. No ejecutar limpieza masiva de recibos o SENT: son evidencia de confirmaci?n. Revisar capacidad de almacenamiento peri?dicamente.

## Rollback

Backend: se puede volver al binario compatible anterior conservando columnas/tablas expansivas; pausar distribuci?n/sincronizaci?n de mobile nuevo si aquel no confirma operationId. No quitar recibos ni ?ndices durante un rollback de aplicaci?n.

Android: una app antigua que solo entiende Room 16 no abre la base 17. Un rollback debe ser otra versi?n firmada con versionCode superior y soporte del esquema 17, aunque revierta comportamiento. No bajar versionCode ni desinstalar para resolverlo. Conservar capturas, referencias de foto y cola; respaldar con procedimiento autorizado del dispositivo antes de cualquier reparaci?n manual.

No se hizo despliegue, firma, modificaci?n de producci?n ni actualizaci?n del tel?fono durante este trabajo.
