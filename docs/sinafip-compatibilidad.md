# SINAFIP: los seis archivos

La pantalla `/#/sinafip` utiliza un servicio de backend para preparar los datos. TXT, Excel y vista previa utilizan exactamente las mismas filas. La generación no modifica saldos, movimientos ni presupuestos.

## Archivos

| Archivo | Reglas principales |
|---|---|
| Apertura inicial | Cuentas marcadas `sigef=1`. Netea Debe menos Haber de los asientos `tipasi=1`; coloca el neto en un solo lado. Período `01`, auxiliar `00` cuando la cuenta no tiene tercer componente. |
| Detalle de apertura | Distribuciones de `benextran`, tipos 4 y 7, agrupadas por cuenta y beneficiario. Conserva las exclusiones del legado, identificación y consumidor final. |
| Balance de comprobación | Apertura neta, movimientos de asientos distintos de apertura acumulados desde enero, totales y saldos deudor/acreedor. |
| Transacciones recíprocas | Tipos 2, 3, 5 y 6 desde `benextran`; 8, 9, 11 y 12 desde `pagoscobros`, con las exclusiones de sus ocho DataWindows. Movimientos del mes: enero comienza el día 2. |
| Presupuesto inicial | Solo partidas con asignación positiva. Agrupa ingresos por clasificador y gastos por `codigo`; conserva actividades separadas de funciones `D*`. |
| Cédula de ingresos y gastos | Inicial, reformas, codificado, comprometido, devengado, cobrado/pagado y saldos acumulados al cierre mensual. Redondea cada ejecución antes de sumar, como `d_esigefejecucion`. |

TXT: campos separados por `|`, coma decimal, dos decimales, sin cabeceras ni BOM y con CRLF final. Excel es un XLSX real y conserva los campos como texto para mantener ceros e identificaciones. El sistema PowerBuilder generaba su hoja electrónica como texto tabulado, no como un libro XLSX nativo.

Las funciones especiales proceden de `estrfunc.funcion LIKE 'D%'`: utilizan el código presupuestario después del prefijo de estructura y agrupan por los primeros 15 caracteres del `codpar`. Se conserva la función `D*`, proyecto `99999999` y el período `1` que el legado escribe en las filas especiales de presupuesto inicial. Las filas normales usan función `000` y período `01`.

Se omiten partidas completamente en cero en la cédula. No se reproducen los errores del legado que escribían una fila vacía si una consulta no tenía registros.

## Recíprocas y asignaciones

La salida tiene 21 campos, en este orden:

1. Período.
2. RUC de la institución.
3. Cuenta, subcuenta y auxiliar (tres campos).
4. Debe y Haber (dos campos).
5. Identificación y nombre del beneficiario (dos campos).
6. Grupo, subgrupo e ítem presupuestario (tres campos; pueden quedar vacíos).
7. Cuenta, subcuenta y auxiliar de contrapartida (tres campos).
8. Debe y Haber de contrapartida (dos campos; invierten los importes originales).
9. Asiento y asiento de referencia (dos campos).
10. Fecha y vencimiento (dos campos, `dd/MM/yyyy`).

Agrupa por tipo de transacción, cuenta SINAFIP, asiento y beneficiario. La cuenta se reduce a nueve caracteres si está marcada SINAFIP; en otro caso utiliza los primeros seis. Conserva la referencia al propio asiento en los tipos 2, 3, 5 y 6. Los pagos/cobros usan `asierefe`; el tipo 9 agrupado y el tipo 12 utilizan referencia `0`, como el legado.

La opción **Revisar / asignar contrapartidas** permite editar `codcuereci`, `codparreci` y `asierefe` de cada distribución. Cada botón **Guardar** actualiza únicamente esos campos de `benextran` o `pagoscobros`; valida el origen y una cuenta marcada SINAFIP. No modifica importes, beneficiarios ni asientos. No hay asignaciones automáticas.

Si falta una cuenta recíproca o una referencia obligatoria, la exportación informa la transacción afectada y espera a que el usuario complete sus datos. El RUC institucional debe estar registrado en `definir`.

## Comparación con el sistema anterior

Se revisó `C:\erp_EpmapaT\c_balances.pbd`, ventana `w_sinafip`, y se recuperaron las funciones `wf_aperturainicial`, `wf_detaperturaini`, `wf_balcomprob`, `wf_preinicial`, `wf_cedulas`, `wf_reciprocas` y `wf_reciprocasplano`. La extracción se realizó localmente con el código MIT de [PbdViewer](https://github.com/Hucxy/PbdViewer). La herramienta y los archivos recuperados permanecen en `target`; no forman parte del backend distribuido.

Referencias encontradas en `C:\erp_EpmapaT\Comprobantes\SINAFIP\2026` y comparación contra el ejercicio configurado, en transacciones PostgreSQL de solo lectura:

| Referencia de enero de 2026 | Resultado |
|---|---|
| `AI_prueba1.txt` | 79 filas anteriores y 78 actuales. Coinciden importes de cuentas comunes y totales. |
| `BC_prueba1.txt` | 153 filas anteriores y 152 actuales. Coinciden importes de cuentas comunes y totales. |
| `DAI_prueba1.txt` | 11 filas; coincide byte por byte. |
| `TR_prueba1.txt` | Referencia vacía. El catálogo actual tiene distribuciones pendientes de cuenta recíproca; la primera detectada es la transacción 433. |
| Presupuesto y cédula | No hay TXT de referencia en esa carpeta. Se implementan las reglas recuperadas de las funciones PowerBuilder y se prueban con datos controlados. |

La diferencia de apertura/balance es del catálogo: las cuentas anteriores `151.58.02` y `151.58.04` no están en el plan actual, que utiliza `151.92`. No se añadieron equivalencias artificiales ni se alteraron cuentas para forzar una coincidencia de filas.

La pantalla permite elegir un TXT anterior del mismo ejercicio/período y comparar localmente sus filas, incluidos duplicados y espacios. El archivo seleccionado no se envía al servidor. Se muestran filas coincidentes y diferencias, ignorando solamente su orden y el tipo de salto de línea.

## Despliegue y verificaciones

Reiniciar/desplegar el backend y actualizar el frontend. No requiere una migración SQL nueva. El ejercicio se valida contra `definir.fechap`; las fechas incluyen todas las horas del último día del mes. Los cálculos monetarios usan `BigDecimal` y la ejecución se agrega antes de unir al presupuesto para no multiplicar la asignación inicial.

Endpoints:

- `GET /sinafip/vista-previa?archivo=balance&anio=2026&periodo=1`.
- `GET /sinafip/reciprocas/asignaciones?anio=2026&periodo=1`.
- `GET /sinafip/cuentas`.
- `PUT /sinafip/reciprocas/asignacion`: `origen`, `idregistro`, `codcuereci`, `codparreci`, `asierefe`.

Pruebas backend: `SinafipServicioTest` y `SinafipConsultaPostgresTest`. La prueba PostgreSQL usa `SINAFIP_TEST_JDBC_URL` y **requiere una base temporal vacía**: crea las tablas de prueba. La comparación optativa `SinafipReferenciaLocalTest` utiliza variables separadas `SINAFIP_READONLY_JDBC_URL`, `SINAFIP_READONLY_USER`, `SINAFIP_READONLY_PASSWORD` y `SINAFIP_LEGACY_REFERENCE`; PostgreSQL impide escrituras durante esa comparación.

Frontend:

```powershell
./node_modules/.bin/ng.cmd test --configuration sinafip --include=src/app/componentes/contabilidad/sinafip/sinafip/sinafip.component.spec.ts --watch=false --browsers=ChromeHeadless
```
