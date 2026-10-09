# Diagnóstico de error 500 y CORS en RRHH

Fecha: 7 de octubre de 2026.

El navegador reporta HTTP 500 sin `Access-Control-Allow-Origin` en consultas de saldos/solicitudes de la persona 2, desde `http://192.168.0.88` hacia `http://192.168.0.165:8080`.

## Evidencia

- Petición OPTIONS al servidor remoto: 200; origen y cabecera Authorization autorizados.
- GET sin token: 401 con cabecera CORS y mensaje `Token WEB requerido`. No se omitió autenticación para leer datos protegidos.
- Consulta de sólo lectura a la base configurada para producción (`192.168.0.46/ErpEpmapaT`): falta `th_leave_balances.version`; faltan `th_leave_requests.resuelto_por`, `fecha_resolucion`, `motivo_resolucion`; no existe `th_leave_movements`.
- El código actual consulta estos campos con Hibernate y requiere esas migraciones. `ddl-auto=none` no los crea automáticamente.

La incompatibilidad de esquema puede provocar los 500 de estas rutas. No se dispone de la traza del backend de la petición autenticada ni de confirmación de posibles variables de entorno que sustituyan la base en el servidor remoto; la causa final debe verificarse con esa traza. El mensaje CORS oculta el error HTTP en el cliente, y permitir otro origen no subsana las columnas ausentes.

## Cambios preparados

El filtro CORS ahora procesa despachos ERROR y errores anidados, incluso si el contenedor reinicia la respuesta. Registrar el filtro para ERROR no bastaba: `OncePerRequestFilter` omite esos despachos por defecto. Mantiene la política de orígenes existente y no elimina JWT ni permisos.

Validación: cuatro pruebas JUnit aprobadas y backend compilado con `mvn -Dtest=CorsConfigTest test`. Se cubren preflight con Authorization, despacho ERROR con HTTP 500, error anidado después de reiniciar la respuesta y respuesta 401 visible con CORS. El proveedor TestNG ejecuta después cero casos para esta clase JUnit; el resultado de las cuatro pruebas consta en `target/cors-regression-tests.log`.

## Corrección de esquema pendiente de aplicación

Con el backend detenido para el despliegue coordinado y usando una copia de respaldo, aplicar los scripts ya existentes, en este orden:

1. [Movimientos y resolución](src/main/resources/sql/2026-10-06_rrhh_leave_movements.sql).
2. [Ajustes del libro](src/main/resources/sql/2026-10-06_rrhh_leave_balance_adjustments.sql).
3. [Versión del saldo](src/main/resources/sql/2026-10-06_rrhh_leave_balance_version.sql).

Los scripts tienen transacciones y fueron ensayados en PostgreSQL temporal. Conservan importes existentes y no reconstruyen consumos históricos. Después, desplegar el backend corregido y verificar saldos/solicitudes con token válido y el origen real, comprobando código HTTP, cabeceras y registros del servidor.

No se aplicaron cambios de esquema ni se desplegó código en producción durante este diagnóstico.
