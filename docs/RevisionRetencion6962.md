# Retencion 6962 - CONSORCIO CODA

Revision: 2026-11-10. ID interno: 181.

## Evidencia consultada en produccion

- fechaemision (documento de sustento): 2026-11-04.
- fechaemiret1 (emision de la retencion): 2026-11-10.
- XML guardado: fechaEmision=04/11/2026; documentos de sustento=04/11/2026.
- Clave guardada: prefijo de fecha 04112026.
- Registro principal: estado=1; sin numautoriza_e ni fecautoriza.
- Registro electronico: NO_AUTORIZADO, sin XML de autorizacion. Solo conserva el mensaje generico de rechazo, sin codigo detallado.

No se modificaron estos registros ni se reenvio el comprobante al SRI. La consulta de lectura al microservicio devolvio un objeto vacio; no permite establecer el motivo especifico del rechazo original. La fecha incorrecta esta comprobada; no se afirma que sea la unica causa de no autorizacion.

## Cambios de codigo

La emision de la retencion utiliza fechaemiret1 (fallback a fechaemision solo para registros historicos que no tengan aquella fecha). La fecha del documento de sustento sigue utilizando fechaemision. No se reemplazan fechas de comprobantes anteriores por la fecha actual del servidor.

XML, clave de acceso, fecha/periodo guardados en fec_retenciones y cabecera del RIDE usan la misma fecha de retencion. La previsualizacion rechaza una clave cuya fecha no coincida, indicando regenerar el XML; el flujo de generar y guardar asegura primero una clave consistente.

## Persistencia al autorizar

Tanto el envio de una retencion nueva como la consulta posterior de una pendiente guardan la respuesta AUTORIZADO mediante `RetencionesServicio.guardarAutorizacionSri`. Actualiza exclusivamente claveacceso, numautoriza_e, numautoriza, fecautoriza, ambiente y estado=1 en retenciones. No vuelve a guardar los datos contables ni las relaciones del asiento. El XML completo se conserva en fec_retenciones en la misma transaccion.

El numero y fecha deben venir de la respuesta SRI; no se completan con autorizaciones anteriores. La clave se extrae del comprobante autorizado y se compara con la retencion solicitada. Pendientes, rechazos y respuestas incompletas no se guardan como autorizaciones.

Una retencion con estado principal=1 solo puede regenerarse sin autorizacion si tiene rechazo electronico NO_AUTORIZADO explicito y no tiene metadatos de autorizacion. Las autorizadas o historicas sin confirmacion siguen bloqueadas. Se evita confundir numautoriza del documento de sustento con numautoriza_e de la retencion.

Al consultar un rechazo se conserva el identificador, mensaje e informacion adicional del SRI en el error guardado y se corrige el estado principal a pendiente solo si no existen metadatos de autorizacion. No se envia un nuevo comprobante al consultar su estado.

## Pasos posteriores al despliegue

1. Consultar la autorizacion SRI de la clave anterior y revisar la respuesta detallada.
2. Para el rechazo confirmado, regenerar XML y clave usando la fecha de retencion registrada. Para 6962: fechaEmision 10/11/2026 y clave iniciada en 10112026; sustento 04/11/2026.
3. Validar el XML y enviar desde el modulo cuando corresponda. No se realizo ese envio durante esta revision.

Pruebas: fecha de retencion distinta de sustento, clave coherente y digito verificador, proteccion de autorizadas, rechazo explicito, conservacion de detalles del SRI y consulta sin reenvio; ademas de las pruebas existentes de XML/RIDE.
