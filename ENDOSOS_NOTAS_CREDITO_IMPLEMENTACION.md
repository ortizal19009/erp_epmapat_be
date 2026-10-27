# Endosos de notas de crédito — implementación y puesta en servicio

Fecha: **19 de octubre de 2026**.

Estado: implementación local, con activación deshabilitada por defecto. **No se ejecutó la migración sobre la base real y no se desplegó el módulo.**

## 1. Decisiones respecto al plan

- Se eligió crear una nota derivada en `ntacredito` por cada cuenta destino. El esquema real admite `nrofactura` nulo; no se inventan facturas ni comprobantes SRI.
- `devengado` sigue representando exclusivamente aplicaciones en cobros. Las transferencias se calculan desde `nc_endoso`.
- El proyecto actual no tiene Flyway. Se entrega una migración SQL explícita para mantenimiento, sin añadir un mecanismo que altere automáticamente producción al arrancar.
- El motivo completo de reversion se conserva en el endoso y en auditoria; su copia en `ntacredito.razoneliminacion` respeta el limite real de 254 caracteres.
- Se verificaron en modo de solo lectura los tipos reales: usuario `smallint`, abonado `bigint`, cuenta en `ntacredito` `integer`, documento `integer`, importes originales `double precision`.
- La migración convierte `valor` y `devengado` a `numeric(14,2)`. Antes comprueba que no haya fracciones de más de dos decimales; si las encuentra, aborta en lugar de redondearlas silenciosamente. En la consulta inicial, las 100 notas existentes no presentaban ese problema.
- Las cuentas habilitadas para endosar/recibir requieren `abonados.estado = 1`. Este criterio debe ratificarse en UAT.
- En v1 no se permite volver a endosar una nota derivada.
- No se reserva saldo al aprobar. Se recalcula bajo bloqueo al ejecutar.
- Por defecto no se permiten titulares diferentes. La institución debe autorizar explícitamente esa política antes de cambiar su configuración.
- Los permisos se conceden expresamente en una tabla dedicada. No se otorgan automáticamente al usuario administrador.

## 2. Archivos principales

| Capa | Implementación |
|---|---|
| Migración | `src/main/resources/sql/2026-10-19_nc_endosos.sql` |
| Operaciones y reglas | `src/main/java/com/epmapat/erp_epmapat/nc/NcEndosoService.java` |
| REST, permisos por acción y exportación | `src/main/java/com/epmapat/erp_epmapat/nc/NcEndosoApi.java` |
| Comprobante y consolidado | `src/main/resources/nc-reports/EndosoNotaCredito.jrxml` |
| Nota individual con saldo y endosos | `src/main/resources/nc-reports/NtaCreditoConEndosos.jrxml` |
| Pruebas PostgreSQL | `src/test/java/com/epmapat/erp_epmapat/nc/NcEndosoIntegrationTest.java` |
| Ejecutor de pruebas aisladas | `scripts/test-nc-endosos.ps1` |
| Pantalla Angular | `../erp_epmapat_fe/src/app/componentes/ntacredito/endosos/` |

## 3. Flujo operativo

1. Desde Notas de crédito, abrir **Endosar saldo**. Ruta: `/#/nc-endosos?nota=ID_INTERNO`.
2. El solicitante consulta la disponibilidad de la nota de origen.
3. Registra respaldo administrativo, referencia, motivo, cuentas destino y valores. Se permiten de 1 a 20 destinos, sin repetidos y distintos de la cuenta de origen.
4. Guarda el borrador. Puede adjuntar un respaldo PDF de hasta 5 MB; el archivo se conserva en base de datos, con nombre saneado y huella SHA-256.
5. Presenta la solicitud. El estado pasa de `BORRADOR` a `PENDIENTE`.
6. Otro usuario con permiso autoriza o rechaza, indicando motivo. No se permite aprobar la solicitud propia.
7. Un ejecutor con permiso confirma el endoso. Se revalidan nota, cuentas, suma, versión, huella aprobada y saldo disponible.
8. En una sola transacción se crean los créditos derivados, se vinculan a los detalles, se registra ejecución, saldos antes/después y auditoría.
9. Las cuentas destino utilizan el saldo en recaudación. La transferencia no paga facturas automáticamente.
10. Puede imprimirse el comprobante individual o exportarse el consolidado a PDF/XLSX.

Solo se editan borradores. Una solicitud presentada o autorizada no se modifica: puede anularse antes de ejecutar y crearse una nueva. Esta decisión evita conservar una aprobación después de cambiar los destinos.

## 4. Saldo y conservación

```text
disponible = valor - COALESCE(devengado, 0)
             - SUM(endosos salientes cuyo estado es EJECUTADO)
```

Un endoso `REVERTIDO` ya no se resta. No se suma además una segunda reversión, evitando duplicar la devolución de saldo.

La vista `nc_saldos` devuelve cero utilizable para notas inactivas o eliminadas. Una nota derivada tiene `tipo_origen = ENDOSO`, nota padre, detalle de procedencia, importe recibido y `nrofactura = NULL`.

El listado de notas obtiene el saldo disponible calculado por el servidor cuando la función está activa; no debe seguir mostrando únicamente `valor - devengado`.

## 5. Seguridad y permisos

La ruta `/api/nc-endosos` exige JWT WEB. El actor se obtiene del atributo autenticado del servidor, no de un ID enviado por Angular. Se verifica que el usuario siga activo.

Permisos almacenados en `nc_endoso_permiso`:

| Permiso | Uso |
|---|---|
| `CONSULTAR` | Listado, disponibilidad, detalle, respaldo e historial |
| `SOLICITAR` | Crear, editar, adjuntar, presentar y anular solicitudes propias |
| `AUTORIZAR` | Aprobar o rechazar solicitudes ajenas |
| `EJECUTAR` | Ejecutar una solicitud autorizada |
| `REVERTIR` | Revertir; actor distinto del solicitante y del ejecutor |
| `REPORTES` | Comprobante y exportación PDF/XLSX |

Para usar una bandeja completa, el operador también necesita `CONSULTAR`, además del permiso de acción correspondiente y el acceso WEB a la ventana de notas de crédito. No se habilita una vía de autoaprobación por ser administrador.

La v1 permite que autorizador y ejecutor sean la misma persona si tiene ambos permisos. Si la institución exige tres actores diferentes, deben segregarse esas asignaciones antes de habilitarla.

## 6. Concurrencia, idempotencia y auditoría

- Crear requiere UUID de solicitud. Los reintentos con la misma clave y contenido devuelven la solicitud existente; si cambia el contenido, se rechazan.
- Ejecutar requiere otro UUID de operación. El mismo reintento devuelve la ejecución existente; otra clave sobre una ejecución terminada se rechaza.
- Cada transición usa versión para detectar pantallas desactualizadas.
- La ejecución bloquea la cabecera y la nota de origen. Recaudación bloquea las notas de la cuenta antes de consultar saldos y aplicar importes.
- Las notas se bloquean por ID ascendente. No se permiten cadenas de endosos en v1.
- La aprobación guarda huella de distribución y respaldo; se vuelve a comprobar al ejecutar.
- Los importes se operan con `BigDecimal` y escala máxima de dos decimales.
- Una restricción diferida comprueba la igualdad entre total y detalles al confirmar la transacción.
- Un trigger protege el saldo frente a actualizaciones que produzcan sobregiro. Otro impide modificar la procedencia de una nota derivada.
- La auditoría registra actor, acción, estados, motivo, huella y distribución. No admite UPDATE ni DELETE. Los endosos no se borran: se anulan o revierten.
- La generación de PDF y la descarga de archivos se realizan fuera de la transacción de ejecución de saldo.

## 7. Reversión

Solo se revierte un endoso ejecutado cuando todos sus créditos derivados siguen íntegros: sin devengado, sin movimientos en `valoresnc`, sin endosos posteriores y activos.

La operación bloquea origen y derivados; anula las notas derivadas, marca el endoso `REVERTIDO`, registra actor independiente, motivo y fecha, y devuelve disponibilidad al origen en la misma transacción. No borra el historial. Los rechazos de reversión desde el endpoint se registran en una transacción de auditoría separada.

Si existe consumo o alteración del crédito recibido, se rechaza la reversión simple. El procedimiento administrativo de compensación queda fuera de esta modalidad: no se crea automáticamente ni se modifica el historial para simularlo.

## 8. Integración con recaudación y reportes

- Ambas pantallas de recaudación suman los saldos positivos de las notas de la cuenta.
- El cobro centralizado toma el bloqueo de la cuenta y consulta el disponible neto de endosos.
- No se permite aplicar crédito a una selección que mezcle cuentas.
- Las escrituras antiguas por `/ntacredito/up`, `/ntacredito/upall`, POST `/valoresnc` y POST `/facxnc` se rechazan cuando los endosos están habilitados: el cobro debe registrarse desde el servicio transaccional.
- También se impide usar el POST de alta de nota para sobrescribir una existente o introducir devengado aplicado.
- El historial individual usa `valoresnc.valor` y `valoresnc.saldo`, no el importe total de crédito de la factura ni su suma de rubros.
- Con endosos activos, el reporte individual usa `nc_saldos`, identifica el origen de las derivadas y añade endosos salientes.
- Los reportes de endosos se sirven desde el endpoint protegido, con plantillas fuera del directorio de reportes genéricos.
- El consolidado se filtra por estado, cuenta origen/destino y fechas de creación. La pantalla agrega filtros de nota, solicitante y monto sobre los resultados cargados; el listado devuelve como máximo 500 solicitudes y lo advierte al alcanzar ese límite.

No se envían correos automáticamente al ejecutar un endoso. La selección de destinatarios y el procedimiento institucional de notificación deben definirse antes de conectar esa automatización. El respaldo y las exportaciones están disponibles para los operadores autorizados.

## 9. Pruebas y límites de validación

Ejecutar desde el backend:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-nc-endosos.ps1
```

El script crea una instancia PostgreSQL temporal en `127.0.0.1:55441`, aplica la migración sobre un esquema de prueba y ejecuta la suite. No carga credenciales ni configuraciones de producción. Se usa un directorio de clases aislado mediante el perfil Maven `nc-tests-isolated` para evitar interferencia del compilador del IDE.

La suite cubre reparto completo, parcial, saldo insuficiente, validaciones de destino/monto, segregación, huella aprobada, idempotencia, rollback al fallar el tercer destino, reversión, carreras entre endosos y protocolo de bloqueo compartido con un consumo, crédito recibido, adjunto y reportes.

El escenario de concurrencia con consumo reproduce el protocolo SQL de cobro; no sustituye una prueba integral de caja, autenticación, numeración, factura electrónica y reversión del cobro. Esas operaciones requieren UAT en una copia representativa del ERP.

La compilación Angular se valida con:

```powershell
npx ng build --configuration development
```

## 10. Puesta en servicio, pendiente de validación institucional

1. Respaldar y probar la restauración. Ensayar la migración en una copia actual del esquema real y conciliar notas y aplicaciones.
2. Confirmar qué cuentas se consideran activas, restricciones entre titulares, máximo de operación, responsables y documentos admitidos.
3. Aplicar `2026-10-19_nc_endosos.sql` en mantenimiento. Es una migración de una sola aplicación, no un script para repetir sobre las tablas ya creadas.
4. Asignar permisos explícitos a usuarios reales verificados. No usar los IDs ilustrativos de los ejemplos como asignación de producción.
5. Probar las dos pantallas de recaudación y bloquear integraciones externas que todavía escriban movimientos mediante las rutas antiguas.
6. Desplegar backend y frontend coordinadamente, manteniendo inicialmente desactivada la función.
7. Habilitar después de UAT:

```properties
erp.nc-endosos.enabled=true
erp.nc-endosos.maximo=1000000.00
# Solo con politica institucional aprobada:
erp.nc-endosos.entre-titulares=false
```

8. Comprobar una operación aprobada por actores diferentes, reintentos, saldo en origen/destinos, uso posterior en caja y reportes.

### Retirada controlada

Para suspender nuevas operaciones se puede desactivar la función, pero **no debe continuarse cobrando con la fórmula antigua si ya hay endosos ejecutados**. Se debe detener también el uso de notas en caja hasta conciliar y decidir la recuperación. Volver simplemente al binario anterior reexpondría saldo endosado como disponible.

No borrar tablas ni notas derivadas como rollback. Una reversión válida usa la operación auditada; si hubo consumos, requiere compensación administrativa y conciliación.


## Resultado de validacion local

- Suite de endosos: 10 pruebas PostgreSQL ejecutadas, sin fallos ni errores.
- Compilacion Angular de desarrollo: aprobada; advertencias de archivos no utilizados del proyecto.
- Se genero y reviso visualmente el PDF de un reparto ficticio de 500,00 y el PDF
  de un credito recibido de 200,00 con aplicacion de 25,00 y saldo restante de 175,00.
- La exportacion XLSX se ejecuto en la prueba de reporte.
- Los datos de prueba son ficticios y se generaron en una instancia temporal.
- No se ejecutaron cobros ni endosos reales, ni se enviaron notificaciones.

- Regresion adicional: 9 pruebas ejecutadas sin errores (filtros JWT y compilacion/optimizar
  reporte de refacturaciones); una prueba PostgreSQL de refacturaciones quedo omitida
  deliberadamente en esa ejecucion al no habilitar su instancia de prueba especifica.


## Continuacion: integracion de aplicacion en caja

Se incorpora `NcAplicacionCreditoServicio`, invocado desde el servicio de recaudacion.
La aplicacion requiere una transaccion existente (`MANDATORY`) y registra conjuntamente
el devengado, el movimiento, el vinculo con la factura y `facturas.valornotacredito`.
No expone un endpoint adicional para consumir notas por fuera del cobro.

Protecciones verificadas:

- Factura y credito deben corresponder a la misma cuenta.
- Una factura con aplicacion vigente no puede volver a consumir credito por reintento.
- Los saldos se consultan despues de bloquear las notas en orden de ID.
- Solo se utilizan las notas incluidas en el conjunto bloqueado; una nota insertada
  concurrentemente no se consume sin proteccion.
- Un fallo al registrar un segundo movimiento revierte tambien el primero y el devengado.
- El saldo neto de endosos se utiliza aunque la creacion de nuevos endosos este desactivada.
- El flujo HTTP se prueba con JWT reales de prueba y usuarios separados para solicitud,
  autorizacion y ejecucion; no se confia en un actor enviado por el cliente.

Resultado: **14 pruebas PostgreSQL aprobadas**, incluyendo los diez escenarios anteriores.
La cuenta origen y las cuentas destino de las pruebas siguen siendo datos ficticios.
No se aplico la migracion ni se modificaron notas/facturas de produccion.

### UAT restante en una copia del ERP

1. Abrir caja con un usuario de pruebas y cargar facturas pendientes de una cuenta destino.
2. Ejecutar un endoso aprobado por otro usuario y comprobar el saldo ofrecido en ambas
   pantallas de recaudacion.
3. Cobrar con credito parcial y luego completo, comprobando recibo, numeracion y saldo.
4. Reimprimir el comprobante y el PDF de la nota; contrastar importes con los movimientos.
5. Intentar repetir el cobro y la ejecucion; comprobar que no se dupliquen aplicaciones.
6. Probar el fallo de generacion electronica segun la configuracion de pruebas del SRI.
7. Revisar por separado la anulacion del cobro y la restitucion del credito; no asumir que
   revertir un endoso sustituye esa operacion.
8. Conciliar saldo original, saldo disponible origen/destinos y consumos. Asignar permisos
   y habilitar en produccion solo despues de completar esta validacion institucional.


Regresion de esta continuacion: **16 pruebas adicionales aprobadas** (8 de importes
 de recaudacion y 8 de filtros JWT), sin fallos ni errores. `git diff --check` no
reporta errores de formato. No hubo cambios de frontend en esta continuacion.


## Actualizacion del 20 de octubre de 2026: copia local autorizada

El usuario identifico explicitamente `localhost:5432`, base `ErpEpmapaT`, como
entorno de pruebas. Las credenciales se obtuvieron de `.env.local`, sin copiarlas
al codigo ni mostrarlas en la conversacion. No se uso el servidor de produccion.

Trabajo realizado:

1. Inspeccion del esquema real local: 98 notas, sin importes con mas de dos decimales;
   no existian las tablas ni la vista de endosos.
2. Respaldo de `ntacredito`, `valoresnc` y `facxnc`, mas archivo del esquema anterior,
   guardados en `target/nc-uat-20261020/` (directorio ignorado por Git).
3. Aplicacion transaccional de `2026-10-19_nc_endosos.sql` en la copia local.
4. Restauracion de estructura previa y datos del respaldo de esas tablas en un
   PostgreSQL temporal separado, sin alterar la copia local durante esa verificacion.
5. Comparacion de las 98 notas originales con el respaldo: `valor` y `devengado`
   coinciden exactamente despues de convertir los tipos monetarios a numeric.
6. Prueba sobre el esquema local migrado con notas/factura temporales: reparto
   500,00 -> 200,00 + 150,00 + 150,00; reintento sin duplicados; aplicacion 25,00;
   saldo destino 175,00; saldo conjunto restante 475,00; rechazo de reversion con uso.
7. Rollback de toda la prueba y comprobacion de conteos originales y ausencia de la
   factura ficticia. No quedaron notas, permisos, endosos ni aplicaciones de prueba.
   Las secuencias de IDs pueden avanzar aunque la transaccion se revierta.

La prueba usa los servicios reales de endoso y aplicacion de credito. No arranca
el ERP completo ni llama al SRI, SMTP, correos u otros servicios externos. Todavia
no sustituye la UAT de apertura/cierre de caja y emision electronica.

Se corrigio ademas la cabecera del cobro: `ncvalor` se valida antes de guardar,
no acepta negativos, fracciones de centavo ni importes mayores que el total a pagar.
La suite de importes paso de 8 a **10 pruebas aprobadas**.

Para repetir las verificaciones de forma controlada:

```powershell
python scripts/verify-nc-uat-local.py --database ErpEpmapaT
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/verify-nc-uat-backup.ps1 -Backup target/nc-uat-20261020/notas-antes-endosos.dump -ExpectedNotes 98
```

El primer comando siempre utiliza 127.0.0.1:5432 y revierte sus registros temporales;
no aplica migraciones. El segundo restaura estructura previa y datos en un cluster
privado temporal (puerto 55443); no restaura sobre la base local del ERP. El respaldo
parcial no incluye las tablas externas referenciadas: recuperar el entorno completo
requiere tambien el esquema/respaldos generales y su procedimiento de restauracion.

La creacion de endosos sigue deshabilitada y no se asignaron permisos persistentes
ni se realizaron cobros reales. La copia local ya tiene el esquema necesario para
continuar la validacion coordinada del backend/frontend y la caja.


## Acceso local del administrador (20/10/2026)

Por solicitud del usuario se habilito el modulo en el perfil `local`, mediante
`erp.nc-endosos.enabled=${ERP_NC_ENDOSOS_ENABLED:true}`. Puede deshabilitarse con
`ERP_NC_ENDOSOS_ENABLED=false`. El valor por defecto fuera del perfil local sigue
siendo deshabilitado.

En la copia autorizada `127.0.0.1:5432/ErpEpmapaT` se otorgaron expresamente
los seis permisos de `nc_endoso_permiso` al usuario activo 1 (`Administrador`),
verificando su perfil `ADMINISTRADOR`. No se modificaron saldos ni endosos.
Esto no crea una excepcion general por perfil: se mantienen los permisos explicitos
y la separacion entre solicitante y autorizador, asi como los controles de reversion.
Las menciones anteriores a modulo deshabilitado y ausencia de permisos persistentes
describen el estado previo a esta habilitacion local. Produccion no fue modificada.
Reiniciar el backend con perfil local para aplicar la configuracion.


## Consulta de endosos (20/10/2026)

- Ruta Angular `/nc-endosos-listado`, accesible desde el menu de Notas de credito
  y desde la gestion de endosos. Por defecto consulta `EJECUTADO`; permite todos
  los estados, cuenta de origen/destino y rango de fechas de creacion.
- API protegida `GET /api/nc-endosos/consulta`: paginacion de 20 registros y maximo
  de 100 por pagina. El resumen calcula cantidad e importes sobre todos los
  resultados, sin duplicar montos cuando hay varios destinos. El importe ejecutado
  vigente excluye solicitudes revertidas y estados pendientes.
- PDF/Excel usan `/api/nc-endosos/reporte` y los filtros aplicados, sin limite de
  pagina. Al filtrar por una cuenta destino se incluyen todos los destinos de los
  endosos encontrados, para conservar el detalle de la transferencia completa.
- El enlace a detalle abre `/nc-endosos?id=...` con su historial. Se mantienen los
  permisos explicitos `CONSULTAR` y `REPORTES`; no se modifican saldos al consultar.


## Actualizacion de acceso (2026-10-27)

El administrador activo ID 1 ahora recibe todos los permisos de Endosos.
Los operadores conservan permisos explicitos por accion en nc_endoso_permiso.
El servicio se habilita por defecto sin configuracion explicita, verifica la
migracion y conserva la deshabilitacion explicita. Las reglas de separacion
de solicitante/autorizador y las validaciones monetarias siguen vigentes.
Ver ENDOSOS_ACCESO_PRODUCCION.md para despliegue y asignaciones.
