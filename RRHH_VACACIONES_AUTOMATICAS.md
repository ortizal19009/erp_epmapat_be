# Vacaciones por aniversario y reposiciones

Regla solicitada: acreditar **30 días** por cada año cumplido de contratación,
usando `personal.fecinicio`. Los saldos existentes se conservan. No se reconocen
aniversarios anteriores a la primera activación ni el mismo día de activación.

## Activación y recuperación

Al iniciar el backend actualizado se crea `th_leave_annual_control` y se guarda una
única fecha de activación (zona America/Guayaquil). No se sustituye en reinicios.
La aplicación necesita permiso para crear esa pequeña tabla de control.

El scheduler corre al completar el arranque y cada día a las 00:10, hora de Ecuador.
Procesa personal activo con fecha de ingreso. Sólo acredita aniversarios posteriores
a la activación, con al menos un año cumplido y no posteriores al fin de contrato.
Si el servidor estuvo apagado, recupera los aniversarios pendientes posteriores a la
activación. Para contratos del 29 de febrero, usa el 28 en años no bisiestos.

Cada acreditación bloquea el empleado y su saldo anual, añade 30 a asignados y
disponibles, y conserva usados. Si no existe saldo para ese año, crea uno en cero
y registra apertura antes del abono. Las acreditaciones son ajustes con motivo
`ANIVERSARIO: ...`, usuario 0 (Sistema) y clave UUID estable por empleado/año.
El mismo evento no se duplica aunque corran varias instancias. No se modifican
movimientos anteriores. Los saldos inactivos se omiten con aviso en el log; saldos
incoherentes con su libro requieren conciliación y no se alteran automáticamente.

Configuración: `RRHH_VACACIONES_AUTOMATICAS_ENABLED` (true por defecto) y
`RRHH_VACACIONES_AUTOMATICAS_CRON` (0 10 0 * * *). Si se desactiva y reactiva,
se mantiene la fecha original de activación y se recuperan eventos pendientes.

## Reposición y uso de días

La opción Asignar días de reposición requiere permiso de escritura, año, días
positivos de hasta dos decimales y motivo. Crea el saldo si falta y registra un
ajuste `REPOSICION: ...` con el usuario autenticado. La clave idempotente se conserva
si una petición falla; una asignación nueva después del éxito recibe otra clave.
Los días usados se conservan y no se permite incrementar un saldo inactivo.

La reposición se incorpora al mismo saldo de vacaciones del año elegido, no a una
bolsa separada. Se consume con solicitudes de tipo VACACION aprobadas y se reintegra
con su reversión. Los permisos y licencias mantienen su regla actual: no consumen
vacaciones. Las vacaciones siguen descontándose del año de inicio de la solicitud;
no se introducen consumos automáticos entre años ni vencimientos automáticos nuevos.

La pantalla muestra disponibles de saldos activos, usados registrados y días
solicitados aún sin descontar; también conserva el detalle por año y el libro.
La búsqueda de empleados admite cédula, nombres y apellidos, con coincidencias
parciales, varios términos y sin distinguir acentos/mayúsculas.

## Verificación y despliegue

`scripts/test-rrhh-postgres.ps1` ejecuta pruebas en un PostgreSQL temporal aislado
en 127.0.0.1:55439. Incluye dos trabajadores concurrentes para un aniversario,
conservación de consumos, reposición repetida y conservación de la fecha de
activación. No usa conexiones de producción.

En el frontend: `ng test --configuration=rrhh --watch=false --browsers=ChromeHeadless
--include=src/app/componentes/rrhh/th-leave/th-leave.component.spec.ts`.

Se deben desplegar backend y frontend. No se han actualizado saldos en producción
durante el desarrollo. La fecha de activación será la del primer arranque del
backend con esta función habilitada en esa base de datos.
