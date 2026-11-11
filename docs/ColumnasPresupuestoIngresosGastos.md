# Columnas de presupuesto de ingresos y gastos

Las tablas de `/pregastos` y `/preingresos` muestran las columnas solicitadas con encabezado de dos filas y un grupo SALDO.

- Gastos: Partida, Denominación, Inicial, Reforma, Codificado, Compromiso, Devengado, Pagado; SALDO: Compromiso, Devengado y Pagado.
- Ingresos: Partida, Denominación, Inicial, Reforma, Codificado, Devengado, Cobrado; SALDO: Devengado y Cobrado.

Codificado = Inicial + Reforma. Cada saldo = Codificado menos la etapa correspondiente. Los saldos negativos se conservan y resaltan. Los totales se calculan con las mismas columnas de las filas, redondeadas a centavos.

Los listados existentes se enriquecen con una consulta agrupada de `ejecucio` por `intpre`: suma de prmiso, devengado y cobpagado. Se usa el mismo criterio de suma del auxiliar y de la consulta presupuestaria de SINAFIP. No se hacen consultas por fila ni se reescriben totales históricos. Las propiedades `ejecCompromiso`, `ejecDevengado` y `ejecCobpagado` son transitorias, sin columnas nuevas en la base.

Las búsquedas, acceso al auxiliar y opciones Modificar/Eliminar se conservan. La tabla permite desplazamiento horizontal y mantiene el encabezado visible. En Gastos se eliminó el doble disparo de Buscar por click y submit.

Validación: 2 pruebas backend sobre enriquecimiento por lote y partidas sin movimientos; 4 pruebas frontend sobre saldos, importes de texto, negativos, suma de centavos y datos de ejecución ausentes.

Desplegar el backend antes del frontend. Si el servidor todavía no proporciona la ejecución, se informa que no puede cargarse el presupuesto completo en lugar de mostrar ceros aparentes. No requiere migración de esquema.
