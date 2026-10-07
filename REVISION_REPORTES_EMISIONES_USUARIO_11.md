# Revisión de reportes de emisiones: usuario 11

Fecha: 7 de octubre de 2026. Frontend `imp-emisiones`; API comprobada: `http://localhost:9080`.

## Permisos consultados

Consulta de sólo lectura en la base configurada en `.env.local`, sin modificar usuarios, permisos ni datos:

- Usuario 11 activo.
- Módulo comercial 1 habilitado para plataforma WEB.
- Ventanas `emisiones`, `facturas`, `lecturas` y `re-facturacion`: permiso 1 (consulta).

La revisión anterior corrigió el botón Aceptar deshabilitado por el control global de sólo lectura, la exigencia de edición para tres plantillas Jasper de consulta y las restricciones de opciones por ID 1. Las operaciones de modificación de emisiones siguen requiriendo edición.

## Comprobación de cada opción

Emisión cerrada utilizada: **249 / 2607**. Fechas: **2026-07-01–2026-07-31**. Preemisión: emisión abierta **250 / 2608**.

| Opción | Reporte | Resultado de consultas reales |
|---|---|---|
| 0 | Lista de emisiones | HTTP 200; 1 emisión en el rango 2607–2607 |
| 1 | Facturas eliminadas | HTTP 200; 11 registros |
| 2 | Emisiones individuales | HTTP 200; 11 anteriores y 11 nuevas |
| 3 | Emisión inicial | HTTP 200; 9 rubros, 1 resumen de m³ y 0 cuentas sin rubros |
| 4 | Emisión final | HTTP 200; 9 iniciales, 7 nuevos, 9 eliminados, 9 actuales y 1 resumen de m³ |
| 5 | Valores emitidos | HTTP 200; 39 registros |
| 6 | Consumos por categoría | HTTP 200; 3 categorías |
| 7 | Refacturación por emisión | HTTP 200; 11 refacturaciones y 11 eliminadas |
| 8 | Refacturación por fecha | HTTP 200; 5 refacturaciones y 5 eliminadas |
| 9 | Ref emisión rubros | HTTP 200; 8 rubros anteriores y 7 nuevos |
| 10 | Ref fecha rubros | HTTP 200; 8 rubros anteriores y 7 nuevos |
| 11 | Resumen emisión | HTTP 200; PDF con firma `%PDF`, 181015 bytes |
| 12 | Refacturaciones | HTTP 200; PDF con firma `%PDF`, 435575 bytes; parámetro de usuario 11 |
| 13 | Refacturaciones emisión | HTTP 200; PDF con firma `%PDF`, 437442 bytes; parámetro de usuario 11 |
| 14 | Preemisión por ruta | Límite de 40 segundos agotado; reintento aislado también agotó 90 segundos. Pendiente |

Los ceros de cuentas sin rubros son un resultado válido de la consulta, no un rechazo de acceso. Los PDF tienen respuesta y firma correctas; su contenido visual no se comprobó en un visor.

La emisión abierta 250 tiene **18.315 lecturas**, confirmado por una consulta de sólo lectura. `PreemisionServicio.reporte` recorre cada lectura y ejecuta el calculador de previsualización por cuenta. Esto es una explicación posible de la demora, no una medición completa de sus consultas ni una prueba de causa única. Las dos consultas agotadas no devolvieron una respuesta 403; no se acredita la preemisión como aprobada ni se cambiaron sus cálculos. Corresponde diagnosticar/optimizar su carga antes de aceptar ese reporte con esta emisión.

## Pruebas del frontend con contexto de usuario 11

**24 pruebas aprobadas** en ChromeHeadless: despacho de las 15 opciones, actor de las dos plantillas de refacturación, autorización de las tres plantillas Jasper con consulta, rechazo sin permiso, bloqueo de mutaciones y plantillas desconocidas, comportamiento de escritor/administrador, errores visibles y botones de reporte habilitados sin anular validaciones.

Comando desde frontend:

```powershell
npx ng test --configuration=emisiones --watch=false --browsers=ChromeHeadless --include=src/app/compartida/write-permission.interceptor.spec.ts --include=src/app/compartida/read-only-ui.service.spec.ts --include=src/app/componentes/emisiones/imp-emisiones/imp-emisiones.component.spec.ts
```

## Límite de la comprobación

No hay navegador conectado al agente ni sesión WEB del usuario 11 disponible para esta revisión. **No se ha iniciado sesión como ese usuario ni se ha certificado el flujo completo de pantalla/PDF con su sesión.** Las consultas reales son pruebas directas de los endpoints existentes, que no exigen JWT WEB; enviar `idusuario: 11` en Jasper identifica metadatos del reporte, no autentica a ese usuario. Las pruebas automatizadas del cliente usan su ID y nivel de consulta como contexto de prueba.

Para completar la aceptación hace falta conectar un navegador con la sesión del usuario 11 y ejecutar las 15 opciones con estos mismos filtros. Los resultados técnicos detallados se guardan en `target/imp-emisiones-audit/resultados.json`; no contienen las filas personales del reporte ni credenciales. No se alteró la base ni se desplegaron cambios.
