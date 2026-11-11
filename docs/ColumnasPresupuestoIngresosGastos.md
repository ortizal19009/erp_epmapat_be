# Columnas de presupuesto de ingresos y gastos

Las tablas de `/pregastos` y `/preingresos` muestran las columnas solicitadas con encabezado de dos filas y un grupo SALDO.

- Gastos: Partida, Denominación, Inicial, Reforma, Codificado, Compromiso, Devengado, Pagado; SALDO: Compromiso, Devengado y Pagado.
- Ingresos: Partida, Denominación, Inicial, Reforma, Codificado, Devengado, Cobrado; SALDO: Devengado y Cobrado.

Codificado = Inicial + Reforma. Cada saldo = Codificado menos la etapa correspondiente. Los saldos negativos se conservan y resaltan. Los totales se calculan con las mismas columnas de las filas, redondeadas a centavos.

Los listados existentes se enriquecen con una consulta agrupada de `ejecucio` por `intpre`: suma de prmiso, devengado y cobpagado. Se usa el mismo criterio de suma del auxiliar y de la consulta presupuestaria de SINAFIP. No se hacen consultas por fila ni se reescriben totales históricos. Las propiedades `ejecCompromiso`, `ejecDevengado` y `ejecCobpagado` son transitorias, sin columnas nuevas en la base.

Las búsquedas, acceso al auxiliar y opciones Modificar/Eliminar se conservan. La tabla permite desplazamiento horizontal y mantiene el encabezado visible. En Gastos se eliminó el doble disparo de Buscar por click y submit.

Validacion: 3 pruebas backend (enriquecimiento por lote, partidas sin movimientos y serializacion con Hibernate5Module); 5 pruebas frontend sobre saldos, centavos y ejecucion ausente o parcial.

Correccion del error de carga: Hibernate5Module omitia las propiedades con @Transient. Se usa el modificador Java transient, que evita persistirlas y permite serializar los getters. No requiere migracion de esquema.

Cuando falten datos de ejecucion, las tablas conservan las partidas y sus importes Inicial, Reforma y Codificado. Las etapas y saldos no disponibles muestran un guion largo; sus totales tambien quedan pendientes para evitar sumas parciales o ceros aparentes. Desplegar backend y frontend actualizados.
