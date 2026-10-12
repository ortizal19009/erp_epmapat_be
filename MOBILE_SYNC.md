# Sincronizacion de lecturas sin perder datos locales

POST /lecturas/mobile/upload mantiene ok, error, total y details. Agrega:

- confirmedIds: IDs cuyo guardado termino sin excepciones.
- pendingIds: IDs no confirmados; puede incluir null para elementos sin ID.
- results: resultado por posicion del lote (index, idlectura, status, httpStatus y message en errores).
- allConfirmed: true cuando todos los elementos fueron confirmados.

HTTP 200 indica que se proceso el lote; no confirma todas las lecturas.

## Reglas para la app movil

1. Guardar cada captura primero en almacenamiento local persistente, como pendiente.
2. Sincronizar la sesion GPS antes de sus lecturas. Enviar su UUID completo en trackingSessionId; conservar el ID local por separado. No inventar UUIDs para reemplazar sesiones existentes.
3. Enviar una copia del lote con Accept: application/json. Conservar los registros locales durante la solicitud.
4. Marcar como sincronizadas solo las capturas con results[index].status = CONFIRMED. Comprobar que idlectura coincide y que la captura local no fue editada desde que se preparo el lote. Si cambio, mantener la nueva version pendiente.
5. Conservar PENDING junto con su mensaje. Un error de UUID requiere corregir la relacion con la sesion y luego reintentar; no borrar la lectura ni sus coordenadas.
6. Si falla la red, vence el tiempo o falta una respuesta valida, conservar todo el lote pendiente. El servidor pudo guardar datos aunque la respuesta no llegue.
7. No reemplazar datos locales pendientes con descargas o eventos WebSocket. Mantener la copia local y resolver diferencias antes de sincronizar.
8. Conservar un respaldo local de las capturas confirmadas segun la politica de la app. No vaciar la tabla ni la cola por recibir HTTP 200.

## Limites

Este cambio entrega confirmaciones por registro en el backend. Requiere implementacion en la app para proteger su almacenamiento local. No agrega control de versiones o deduplicacion de solicitudes: un reintento puede repetir auditorias y una captura vieja puede sobrescribir una nueva si la app la vuelve a enviar. Evitar solicitudes simultaneas para la misma lectura y comparar versiones locales antes de aplicar confirmaciones. Para resolver concurrencia entre dispositivos se necesita un contrato adicional de versiones.
