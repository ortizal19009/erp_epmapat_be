# Notas de crédito: estado y funcionamiento

Fecha de revisión: **19 de octubre de 2026**.

## 1. Alcance y estado actual

Este documento describe la implementación encontrada en `erp_epmapat_be` y en el frontend hermano `erp_epmapat_fe`. Incluye el alta, la consulta, la aplicación en cobros, el envío de notificaciones y los reportes relacionados.

**Tipo de verificación:** revisión del código y de las consultas Jasper. No se crearon notas, no se realizaron cobros ni se enviaron correos durante esta revisión. La existencia de una función en el código no confirma su despliegue ni su funcionamiento de extremo a extremo en producción.

| Función | Estado observado |
|---|---|
| Registrar una nota desde una factura | Implementado en `/add-ntacredito` |
| Consultar notas y saldos | Implementado en `/ntacredito` |
| Buscar y filtrar el listado | Implementado en el navegador |
| Usar crédito al cobrar una cuenta | Implementado; existen dos flujos de recaudación |
| Registrar aplicaciones a facturas | Implementado mediante `valoresnc` y `facxnc` |
| Imprimir una nota con historial | Implementado con `NtaCreditoIndividual` |
| Notificar por correo después del alta | Implementado, opcional, con PDF y envío a cola |
| Generar notas al anular convenios | Implementado desde el frontend |
| Transferir una nota | Botón deshabilitado en el listado revisado |
| Reporte consolidado exclusivo de notas por período | No identificado en el módulo revisado |
| Autorizar una nota electrónica en el SRI desde este alta | No se encontró una llamada a ese proceso en el flujo revisado |

La nota documentada aquí representa un **saldo a favor de una cuenta**. No debe darse por autorizado un comprobante electrónico del SRI únicamente porque exista el registro en `ntacredito` o su PDF interno.

## 2. Conceptos y saldos

| Dato | Significado en el sistema |
|---|---|
| `valor` | Importe original concedido en la nota |
| `devengado` | Importe acumulado que ya se utilizó |
| Saldo | `valor - devengado` |
| Cuenta | Abonado al que pertenece el crédito |
| Planilla | Identificador interno de la factura de origen |
| `nrofactura` en `ntacredito` | En el alta revisada guarda el ID interno de la factura, aunque el nombre del campo sugiera un número comercial |
| Documento y referencia | Respaldo administrativo de la creación de la nota |

Ejemplo: una nota de **100,00**, con **35,00 devengados**, mantiene **65,00 disponibles**. Si se aplican otros **20,00**, el devengado pasa a **55,00** y el saldo a **45,00**.

El listado muestra «Con saldo a favor» cuando el saldo es mayor que cero y «Pagada totalmente» cuando es cero o negativo. Los filtros «Usadas totalmente» y «Pagadas totalmente» ejecutan la misma condición: saldo menor o igual que cero. Esta etiqueta no acredita una devolución de efectivo al cliente.

## 3. Cómo añadir una nota

1. Ingresar al listado `/#/ntacredito` y abrir la opción de añadir (`/#/add-ntacredito`).
2. Buscar la factura por número o por planilla.
3. Verificar cliente, cuenta y factura recuperados.
4. Revisar el valor propuesto, calculado mediante el servicio de rubros de la factura. Se permite disminuirlo dentro del máximo calculado.
5. Seleccionar el documento de respaldo y completar referencia y observación.
6. Confirmar el registro en el mensaje de confirmación.
7. Elegir si se desea enviar la notificación por correo.
8. Volver al listado y comprobar valor, devengado y saldo; imprimir el documento si corresponde.

### Validaciones visibles del formulario

- Debe haberse cargado una factura con valor mayor que cero.
- Valor obligatorio, mínimo **0,01** y máximo dinámico igual al valor calculado de la factura.
- Documento obligatorio.
- Referencia obligatoria, máximo **30 caracteres**.
- Observación obligatoria, entre **10 y 300 caracteres**.
- El indicador `guardando` evita repetir el envío desde el mismo formulario mientras se procesa.

Al guardar se asignan `devengado = 0`, usuario y fecha de creación, cliente, cuenta y respaldo documental.

**Diferencia entre búsquedas:** al buscar por planilla se comprueba `pagado = 1` y `estado = 1` o `3`. En la rama que busca por número se exige una coincidencia única, pero no se repite esa comprobación antes de llamar a `setFactura`. No se debe asumir que ambas rutas aplican las mismas restricciones.

## 4. Consulta y administración

El listado permite consultar cuenta, cliente, planilla, identificación, valor, devengado y saldo. Dispone de paginación, ordenamiento y búsqueda por coincidencia de texto; el filtro también contempla importes y saldo.

Cuando se necesita filtrar el conjunto completo, el componente obtiene las páginas restantes y filtra en memoria. Esto permite buscar fuera de la página visible, pero puede aumentar las solicitudes y el consumo de memoria si crece mucho el historial.

En las acciones del listado se encuentra **Imp Doc**. La opción **Transferir** aparece deshabilitada. Aunque la entidad tiene campos de transferencia y eliminación, eso no demuestra que exista un procedimiento operativo completo para esas acciones.

## 5. Uso del crédito en recaudación

### Flujo con backend centralizado: `add-recauda`

1. Seleccionar la cuenta y las facturas pendientes.
2. La pantalla consulta `/ntacredito/saldosNC?cuenta=...` y presenta el aviso de crédito disponible.
3. Revisar el valor de crédito propuesto y el importe que deberá cubrirse con otro medio.
4. Confirmar el cobro.
5. Comprobar el comprobante, el devengado de la nota y el saldo remanente.

El backend toma `recaudacion.ncvalor` como importe pendiente de distribuir. Por cada factura aplica como máximo el menor entre el total de la factura y el crédito pendiente. El total de cobro considera los componentes calculados por recaudación, incluidos interés e IVA según corresponda; no equivale a la base sin intereses del reporte de refacturaciones.

Para cada aplicación positiva:

1. Guarda el crédito aplicado en `facturas.valornotacredito`.
2. Busca las notas de la cuenta con saldo disponible.
3. Incrementa `ntacredito.devengado` por el importe usado de cada nota.
4. Crea un movimiento en `valoresnc`: nota, importe aplicado, fecha, saldo posterior y estado `1`.
5. Crea el vínculo entre movimiento y factura en `facxnc`.

Una factura puede consumir más de una nota. Si no se consigue cubrir el importe solicitado con los saldos encontrados, el método lanza una excepción. La revisión no sustituye una prueba de rollback del cobro completo.

**Limitación actual de la pantalla:** `buscarNtaCredito()` usa `lista[0].saldo`, no la suma de todas las notas. Si la primera está agotada y otra tiene saldo, la pantalla puede informar que no existe crédito disponible. El backend, en cambio, recorre varias notas al registrar el uso. La consulta de saldos tampoco define un orden, por lo que no hay una regla FIFO garantizada.

### Otro flujo de recaudación

También existe lógica en `recaudacion/recaudacion.component.ts` que registra movimientos, vínculos y actualización del devengado mediante llamadas separadas desde el navegador. Debe comprobarse qué pantalla utiliza cada operación antes de dar por idéntico su comportamiento al de `add-recauda`.

## 6. Notas desde la anulación de convenios

La pantalla de anulación permite seleccionar facturas pagadas y completar documento, referencia y observación para generar notas.

El frontend guarda una nota por factura seleccionada; después actualiza las facturas del convenio y solicita el cambio de estado del convenio. Son varias peticiones, no una única operación de backend observada en este flujo. Si falla una etapa posterior, deben revisarse las notas ya creadas antes de repetir el proceso para evitar duplicaciones.

## 7. Notificación por correo

Después del alta manual se ofrece enviar una notificación. Es posible indicar varios destinatarios separados por coma o punto y coma; se eliminan duplicados y se valida el formato básico.

El mensaje contiene referencia de la nota, cuenta y valor, y adjunta el PDF `NtaCreditoIndividual`. Se envía mediante `OutboxEmailService` a la cola de correos.

- La confirmación de ingreso a la cola no confirma entrega al destinatario.
- Si se cancela el envío, la nota permanece guardada.
- Si falla la generación del adjunto o el envío, se informa que la nota se guardó y que falló la notificación.
- No se encontró en el listado revisado una acción específica para reenviar esa notificación.

## 8. Reportes e impresión

| Reporte o salida | Uso y alcance |
|---|---|
| `NtaCreditoIndividual.jrxml` | Documento individual con datos de la nota, cliente, cuenta, factura de origen, respaldo y tabla de aplicaciones |
| PDF adjunto al correo | Reutiliza `NtaCreditoIndividual`; no es otro formato independiente |
| `CompPagoConsumoAgua.jrxml` y variante con subtotales | Comprobantes que incorporan el crédito aplicado a la factura |
| `CompPagoServicios.jrxml` | Comprobante de servicios con descuento de crédito aplicado |
| `CompPagoConvenios.jrxml` | Comprobante de convenio que incorpora el crédito |
| `RubrosCobrados.jrxml` y `RubrosCobradosRec.jrxml` | Reportes de caja/recaudador que incorporan el importe de notas en los resultados de cobro |
| `NotasCobradas.jrxml` | Reporte de facturas/notas cobradas con campo de crédito aplicado; su nombre no debe confundirse con un catálogo exclusivo de notas de crédito emitidas |

Para imprimir una nota: abrir el listado, usar **Imp Doc** y revisar el PDF. El frontend solicita `reportName = NtaCreditoIndividual`, parámetros `idntacredito` e `idusuario`, y extensión `.pdf`.

### Hallazgos del PDF individual

- La factura de origen se enlaza con `nc.nrofactura = f.idfactura::text`, consistente con el ID que guarda el alta manual.
- El historial consulta `facxnc`, `valoresnc`, `facturas` y `rubroxfac`.
- La columna de importe del historial usa **`f.valornotacredito`**, el total aplicado a la factura, en lugar de **`vnc.valor`**, la parte aportada por esta nota. Cuando una factura consume varias notas, puede atribuirse a cada una el total de la factura.
- El campo llamado `saldo` en ese subdataset es una suma de rubros de la factura, no `vnc.saldo`. No debe interpretarse como saldo remanente de la nota sin revisar su presentación.
- No se identificó una opción Excel específica del módulo de notas. La impresión individual revisada solicita PDF.

## 9. Datos y endpoints principales

| Tabla | Responsabilidad |
|---|---|
| `ntacredito` | Cabecera, importe original, devengado, cuenta, cliente y respaldo |
| `valoresnc` | Movimiento de aplicación y saldo posterior |
| `facxnc` | Relación entre movimiento y factura beneficiada |
| `facturas` | Factura de origen o de destino; crédito aplicado en `valornotacredito` |
| `recaudacion` | Crédito solicitado en el cobro mediante `ncvalor` |

Rutas relativas al backend; el prefijo externo depende de la configuración de API/gateway:

| Método y ruta | Operación |
|---|---|
| `POST /ntacredito` | Guardar una nota |
| `GET /ntacredito` | Listar todas |
| `GET /ntacredito/all?page=0&size=15` | Listar por páginas |
| `GET /ntacredito/{id}` | Consultar una nota |
| `GET /ntacredito/saldosNC?cuenta=...` | Obtener saldos por cuenta |
| `PUT /ntacredito/up?idntacredito=...` | Actualizar devengado |
| `PUT /ntacredito/upall?idntacredito=...` | Actualizar varios campos de cabecera |
| `POST /facxnc` | Registrar vínculo de aplicación y factura |
| `GET /facxnc/factura?idfactura=...` | Consultar aplicaciones vinculadas a una factura |

## 10. Pendientes detectados y prioridad sugerida

Estos puntos se documentan; **no se modificó su implementación en esta tarea**.

| Prioridad | Pendiente | Evidencia o efecto |
|---|---|---|
| Alta | Validar el alta en backend | El controlador recibe la entidad y el servicio llama a `save`; no se observan allí las restricciones del formulario, control de notas previas por factura ni límite acumulado concedido |
| Alta | Unificar la validación por número y por planilla | Las dos ramas de búsqueda no verifican de la misma manera pago y estado |
| Alta | Mostrar el saldo total realmente utilizable | La pantalla toma únicamente el primer resultado de saldos |
| Alta | Excluir notas anuladas del saldo utilizable | La consulta de saldos no filtra `fechaeliminacion`; tampoco normaliza `devengado` nulo con `COALESCE` |
| Alta | Corregir el historial del PDF | Usar el movimiento de la nota para importe aplicado y saldo posterior; comprobar casos con varias notas por factura |
| Alta | Comprobar concurrencia y reversión | La entidad revisada no tiene `@Version`; no se verificó un procedimiento completo que restituya saldo al anular un cobro |
| Media | Consolidar los flujos de recaudación | Existen escrituras centralizadas y escrituras coordinadas desde frontend |
| Media | Hacer atómica o recuperable la anulación de convenios | Las notas y la anulación se ejecutan en solicitudes separadas |
| Media | Definir regla de consumo de varias notas | La consulta actual no garantiza orden de aplicación |
| Media | Revisar actualización de transferencia | `/upall` asigna `idtransfernota` desde `getIdntacredito()`, no desde `getIdtransfernota()` |
| Media | Añadir reportes consolidados de notas | Emitidas, aplicadas, saldo disponible y anuladas por período/cuenta, con conciliación y exportación |

## 11. Comprobaciones recomendadas antes de cerrar el módulo

1. Crear una nota parcial sobre factura pagada y comprobar el saldo inicial.
2. Comparar búsquedas por número y por planilla sobre la misma factura.
3. Intentar duplicar una nota y superar el importe acumulado de origen en un ambiente de prueba.
4. Aplicar crédito menor, igual y mayor que el total a pagar.
5. Probar una cuenta con varias notas, incluida una primera nota agotada.
6. Pagar una factura con dos notas y conciliar cada movimiento, el devengado y el PDF individual.
7. Comprobar que una nota anulada no se ofrezca para cobro.
8. Probar dos cobros simultáneos sobre el mismo saldo y la reversión de uno de ellos.
9. Comprobar correo válido, correo inválido y fallo de cola sin duplicar el alta.
10. Conciliar por nota: `valor - devengado = saldo`; contrastar el devengado con los movimientos vigentes según la regla de anulación definida.

## 12. Fuentes de la revisión

Rutas del backend:

- `src/main/java/com/epmapat/erp_epmapat/controlador/NtacreditoApi.java`
- `src/main/java/com/epmapat/erp_epmapat/servicio/NtacreditoServicio.java`
- `src/main/java/com/epmapat/erp_epmapat/repositorio/NtacreditoR.java`
- `src/main/java/com/epmapat/erp_epmapat/modelo/Ntacredito.java`
- `src/main/java/com/epmapat/erp_epmapat/servicio/recaudacion/RecaudacionCobroServicio.java`
- `src/main/java/com/epmapat/erp_epmapat/controlador/FacxncApi.java`
- `src/main/java/com/epmapat/erp_epmapat/repositorio/FacxncR.java`
- Plantillas citadas en `src/main/resources/reports/`.

Rutas relativas al frontend `../erp_epmapat_fe/`:

- `src/app/componentes/ntacredito/ntacredito.component.ts` (incluye la plantilla activa en línea).
- `src/app/componentes/ntacredito/add-ntacredito/add-ntacredito.component.ts`
- `src/app/componentes/recaudacion/add-recauda/add-recauda.component.ts`
- `src/app/componentes/recaudacion/recaudacion/recaudacion.component.ts`
- `src/app/componentes/convenios/anular-convenio/anular-convenio.component.ts`
- `src/app/servicios/ntacredito.service.ts`
- `src/app/app-routing.ts`


## Actualizacion posterior: implementacion local de endosos

El plan adjunto del 19 de octubre dio lugar a una implementacion local descrita en
[ENDOSOS_NOTAS_CREDITO_IMPLEMENTACION.md](ENDOSOS_NOTAS_CREDITO_IMPLEMENTACION.md).
Los hallazgos anteriores representan la revision previa: se corrigieron el saldo
sumado en ambas pantallas de recaudacion, el historial por movimiento del PDF,
el uso de notas anuladas y la asignacion de idtransfernota. El nuevo flujo incorpora
saldo endosado separado de devengado, permisos explicitos y creditos derivados.
No se ha aplicado la migracion ni habilitado el modulo en produccion.
