# Revisión funcional de los submódulos de Contabilidad

Fecha: 2026-11-10. Alcance: los siete accesos del menú Contabilidad y sus opciones relacionadas. Se revisaron frontend, rutas, servicios HTTP, controladores, persistencia y pruebas. Las consultas al servidor fueron de solo lectura; no se crearon ni modificaron asientos, movimientos, cuentas o conciliaciones reales.

## Inventario y resultado

| Submódulo | Opciones revisadas | Resultado |
| --- | --- | --- |
| Cuentas | Buscar, detalle, crear/desagregar, modificar, eliminar, mayor, imprimir/exportar y Jasper | Listado disponible. Se liberó el estado de búsqueda ante errores. Eliminación y cálculos requieren pruebas de aceptación con perfiles y datos de ensayo. |
| Diario general | Buscar asientos/comprobantes, transacciones, crear/modificar, eliminar, acceso a retenciones, imprimir/exportar | Consulta disponible. Se liberó la búsqueda ante errores y se hizo transaccional el alta de transacción con recálculo de totales. |
| Control de bancos | Buscar movimientos, marcar/desmarcar conciliación, modificar documento, resumen de conciliación e impresión | Varias opciones tenían fallos confirmados; corregidos según el detalle siguiente. Falta definir el tratamiento de ejercicios y movimientos pendientes. |
| SINAFIP | Vista previa, TXT/XLSX, asignaciones recíprocas y comparación con referencia | Catálogo disponible y pruebas existentes aprobadas. No se modificaron formatos ni reglas contables. |
| Plan de cuentas NIIF | Buscar, crear/editar cuenta, homologar/desvincular e imprimir | Corregidos método HTTP de edición, respuesta de eliminación, filtros ausentes y recuperación tras error de búsqueda. |
| Balance general | Cargar catálogo/niveles, calcular, PDF y Excel | Consulta de agregados disponible. Hay agrupaciones fijas que requieren conciliación con una referencia contable antes de cambiar fórmulas. |
| Estado de resultados | Cargar catálogo/niveles, calcular, PDF y Excel | Consulta disponible. Misma necesidad de validar agrupación y saldos contra una referencia. |

Tesorería, Presupuesto y Costos son grupos separados en el menú; no se presentan como íntegramente auditados en esta revisión. Las correcciones previas de retenciones se conservan.

## Correcciones implementadas

### Bancos

1. El checkbox enviaba booleanos a `Transaci.swconcili`, que es Integer. Ahora envía 0/1.
2. Usaba `getMonth()` (0–11) de la fecha actual en lugar del mes seleccionado. Ahora utiliza el mes del filtro (1–12).
3. `TransaciServicio.updateTransaci` omitía `swconcili` y `mesconcili`. Ahora persiste ambos, valida sus valores y pone mes=0 al desmarcar.
4. El frontend envía solo conciliación, usuario y fecha; no reenvía valor, cuenta ni debe/haber de una fila potencialmente desactualizada.
5. Si falla el guardado se restaura el estado de la fila.
6. «Aceptar» del modal Modificar no tenía acción de guardado. Ahora persiste documento/número/descripción y conserva el importe.
7. «Aceptar» del modal Conciliación tampoco guardaba. Ahora actualiza un registro existente, conserva su ID y verifica cuenta/mes. Si no existe, informa el problema y evita guardar datos vacíos o de una consulta anterior.
8. `ConciliabanService` llamaba rutas PUT y `/saveconcilia` no implementadas por el backend. Se usa el POST `/conciliaban` existente, con ID para actualización.
9. El saldo bancario se calculaba con el `banfinal` anterior. Ahora se calcula a partir del nuevo valor. El saldo en libros vuelve a descontar depósitos y se convierten entradas de texto a números.
10. Se tolera un catálogo de bancos vacío y se inicializan formularios antes de pedir datos.

### NIIF y navegación

- Editar cuenta NIIF enviaba POST a `/niifcuentas/upd/{id}`, pero el controlador requiere PUT. Corregido.
- Eliminar homologación comprobaba si un Optional era null, por lo que devolvía false siempre. Ahora utiliza `isEmpty()`.
- Listar NIIF sin nombre producía null.toLowerCase(). Se normalizan filtros ausentes.
- Se quitaron las rutas duplicadas `bancos` y `conciliaban`.
- Se restablecen indicador y texto Buscar ante errores en Cuentas, Diario y NIIF.

### Diario

El alta de una transacción y su recálculo del asiento ahora comparten transacción de base de datos. Si falla el recálculo, la nueva línea no debe quedar confirmada separadamente. No se modificaron reglas de redondeo ni se recalcularon registros históricos.

## Pendientes que requieren definición o referencia

1. **Bancos por ejercicio y pendientes:** `findMovibank` filtra únicamente por `mesconcili`; no recibe año ni incluye por sí mismo los no conciliados de mes=0. Debe definirse si mostrar movimientos del período, pendientes anteriores o ambos, y cómo identificar el ejercicio. No se cambió silenciosamente la selección.
2. **Alta de conciliación mensual:** esta revisión permite guardar una conciliación existente. Crear una nueva requiere definir de dónde se calculan/importan los saldos iniciales, débitos y créditos. No se crea un registro con saldos cero por un 404.
3. **Balance y resultados:** el frontend utiliza grupos fijos y `transaci/estados` consulta cuentas/transacciones, sin aplicar la tabla `niifhomologa` en ese camino. Estar bajo el menú Homologación NIIF no certifica que sean estados NIIF homologados. Comparar con un período y reporte de referencia antes de modificar agrupaciones o signos.
4. **Carga de catálogos de estados financieros:** cuentas y niveles se cargan en suscripciones separadas; conviene unificar su carga y bloquear cálculo/exportación hasta completarla.
5. **Permisos y eliminaciones:** validar con perfiles de consulta/edición y operaciones de ensayo las opciones inline y restricciones de borrado. No se realizaron escrituras reales para comprobarlas.

## Consultas de disponibilidad en producción

Todos devolvieron HTTP 200:

| Consulta acotada | Filas |
| --- | ---: |
| `/cuentas/lista?codcue=111&nomcue=` | 49 |
| `/cuentas/bancos` | 16 |
| `/asientos?asi_com=1&desdeNum=1&hastaNum=3&desdeFecha=2026-01-01&hastaFecha=2026-12-31` | 3 |
| `/niifcuentas?codcue=1&nomcue=` | 1090 |
| `/niveles` | 7 |
| `/sinafip/cuentas` | 1941 |
| `/transaci/estados?intgrupo=111&desdeFecha=2026-11-10&hastaFecha=2026-11-10` | 4 |
| `/transaci/estados?intgrupo=6621&desdeFecha=2026-11-10&hastaFecha=2026-11-10` | 0 |

Esto acredita disponibilidad de consultas, no exactitud contable integral ni todos los permisos de escritura.

## Validación

- 12 pruebas backend: conciliación parcial sin cambiar importes, rechazo de mes inválido, opciones NIIF y las pruebas de SINAFIP.
- 13 pruebas frontend: bancos (selección de mes, guardado, reversión ante error, saldos y catálogo vacío), método PUT NIIF y SINAFIP.
- TypeScript del frontend compilado sin errores.
- Sin cambios de esquema ni correcciones masivas de datos.

Desplegar backend y frontend para aplicar el conjunto. Las pruebas de aceptación sobre datos de ensayo deben cubrir selección de mes, marcar/desmarcar, reabrir para comprobar persistencia, modificar número/documento, guardar conciliación existente y editar/desvincular NIIF.
