# Revisión de anulaciones, bajas, reasignaciones y trámites de agua

Fecha: 2026-11-10. Frontend: `http://192.168.0.88/`; API configurada en el bundle publicado: `http://192.168.0.165:8080`.

## Error posterior en backend local, puerto 9080

El log de las 13:26:08 muestra `no existe la columna lecturas0_.promedio` durante GET del detalle de anulación, en `LecturaServicio.findByIdfactura` → `FacturaServicio.obtenerDetalleAnulacionBaja`. Ocurre antes de ejecutar la anulación. La entidad `Lecturas` ya incluye el campo, pero la migración `sql/2026-11-04_lecturas_promedio.sql` no se ejecutaba al arrancar y Hibernate está configurado con `ddl-auto=none`.

Se incorpora `LecturasPromedioSchemaMigration` para crear de forma idempotente la columna `numeric(14,2)` al arrancar. Conserva NULL en lecturas históricas; no recalcula ni inventa promedios. El índice del script de despliegue sigue siendo opcional para esta corrección. Requiere reiniciar con el backend actualizado y permisos de ALTER en su base configurada. Este fallo de esquema es distinto de las dependencias electrónicas identificadas en producción.

También se aplicó únicamente el ADD COLUMN idempotente en la base de `localhost` configurada en `application-local.properties`. Se verificó tipo numeric(14,2) y la lectura 3028466 conservó promedio NULL. Sin reiniciar el proceso, GET `http://localhost:9080/facturas/3084458/anulacion-baja-detalle` respondió HTTP 200 con la factura y una lectura. Compilación backend correcta. No se ejecutó POST de anulación.

## Error de aliases duplicados en la aplicación del trámite

El siguiente log identifica `NonUniqueDiscoveredSqlAliasException` en `AguaTramiteAplicacionServicio.aplicar`: los tres resultados de `set_config` tenían el mismo alias implícito. Se asignan nombres distintos (`historial_modulo`, `historial_usuario`, `historial_observacion`) tanto en ese servicio como en la actualización general de `AbonadoServicio`, que tenía el mismo patrón. No requiere cambios de esquema; requiere cargar las clases actualizadas en el backend. La prueba `HistorialContextoPostgresTest` ejecuta ambas consultas reales mediante Hibernate y PostgreSQL local dentro de una transacción revertida, con las escrituras de cuentas/trámites simuladas.

## Evidencia de la factura 3084458

Consultas de solo lectura a la API y PostgreSQL:

- Cobrada el 2026-11-04, pagado=1, estado=1, forma de pago=6.
- Número 001-025-000071510; no tiene fecha ni motivo de anulación.
- Comprobante electrónico en estado A.
- Existe un registro de esta factura en cada tabla: `sri_autorizacion_control`, `comprobante_documento`, `evento_outbox`.
- Las tres tablas tienen claves foráneas hacia `fec_factura` sin `ON DELETE CASCADE`. El código anterior solo eliminaba impuestos, detalles, pagos y logs antes de borrar `fec_factura`.

Esto identifica un impedimento concreto para la eliminación electrónica. No se ejecutó una anulación en producción ni se reprodujo el HTTP fallido mediante una escritura. El código HTTP y el log de la petición original siguen pendientes; no se afirma haber probado la respuesta 502/503/504 del incidente.

## Flujo de facturas

| Operación | Petición | Efecto |
| --- | --- | --- |
| Nueva anulación | POST `/facturas/anulaciones-bajas`, acción ANULACION | Audita factura, registra fecha/motivo/usuario, limpia número y cobro y vuelve a pendiente; respalda y elimina estructura electrónica dentro de la transacción. |
| Nueva baja | Mismo endpoint, acción ELIMINACION | Registra eliminación lógica, estado=0; en módulos 3/4 actualiza la primera lectura asociada con auditoría. |
| Nueva reasignación | POST `/facturas/reasignar-numero` | Exige factura cobrada con número y recaudador; bloquea secuencial de caja, reserva número, regenera comprobante y guarda historial. |

La corrección respalda las tres dependencias adicionales mediante auditoría atómica antes de eliminarlas. Si falla el respaldo, se aborta la operación. En reasignaciones también se retiran los documentos y trabajos asociados al número anterior para que no se reutilicen tras regenerar el comprobante. Se admite la ausencia de estas tablas en instalaciones antiguas.

El log de producción de las 14:12:46 identificó un fallo en el respaldo JSON: Hibernate 5 interpreta el operador PostgreSQL `::jsonb` al analizar parámetros y produce SQL inválido. Se sustituye `'[]'::jsonb` por `CAST('[]' AS jsonb)`. La prueba PostgreSQL ejecuta las consultas de respaldo y borrado con Hibernate sobre tablas temporales, comprueba conservación de XML/RIDE en el respaldo, mantiene otra factura y cubre tablas sin registros. Toda la prueba se revierte; no anula facturas reales.

La anulación implementada es una operación interna del ERP: este endpoint no invoca una cancelación externa del SRI. Se mantiene el comportamiento previo del sistema.

Después de la anulación, el frontend aún registra `facturamodificaciones` mediante otra petición y recarga históricos mediante varias consultas. Un fallo en esas peticiones puede mostrar un error aunque la primera operación haya terminado. Para la factura revisada, la fecha de anulación continúa vacía.

## Retiro de medidor y cambio de categoría

Antes: guardar trámite terminado → generar PDF (incluye consultas de personal/usuario) → actualizar cuenta con auditoría → enviar correo. Un fallo antes de la actualización dejaba el trámite registrado sin aplicar la cuenta.

Ahora, para tipos 2 (retiro) y 9 (categoría):

1. El frontend prepara los datos sobre una copia de la cuenta.
2. POST `/aguatramite/aplicar` recibe trámite y cambios de cuenta.
3. El backend valida que sea un trámite nuevo de tipo admitido y que ambas cuentas coincidan; bloquea la cuenta y registra auditoría en la misma transacción.
4. Retiro: solo admite estado actual 1 o 2 y establece estado 3. Categoría: actualiza categoría, adulto mayor y municipio. Conserva los demás campos de la cuenta.
5. Guarda cuenta y trámite terminado juntos. Un fallo revierte ambos.
6. Tras confirmar el guardado, genera PDF y envía correo. Un fallo del PDF se comunica como advertencia y no impide aplicar la cuenta.

Se establece el contexto `TRAMITES_AGUA` para el historial de base de datos. Ante un error de guardado, el frontend conserva la cuenta original y permite reintentar.

## Validación y despliegue

Validación local: 17 pruebas backend sin fallos (dependencias SRI, transacciones, aplicación de trámites, cuentas, motivos y actualización de factura); 3 pruebas frontend en ChromeHeadless sobre retiro, categoría y fallo de guardado/PDF. La ejecución frontend usa la configuración `aguatramite` y un tsconfig acotado: la configuración general presenta errores previos en specs de clientes/retenciones y carga AdminLTE sin el DOM que requiere.

Los cambios se prepararon localmente en los repositorios backend y frontend. Desplegar primero el backend que exponga `/aguatramite/aplicar` y después el frontend. No se cambió el servidor ni se modificaron registros reales.

Verificar después del despliegue con cuentas de prueba: retiro desde activo/suspendido; rechazo de retiro repetido; cambio de categoría manteniendo estado/medidor; fallo de PDF posterior al guardado; anulación con dependencias electrónicas y respaldo de XML/RIDE; reasignación con nuevo número e historial sin documentos del número anterior.

Los trámites históricos que ya quedaron incompletos requieren conciliación individual; este cambio no los aplica retroactivamente.
