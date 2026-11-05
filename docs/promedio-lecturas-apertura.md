# Promedio de consumo por lectura

Antes de iniciar la nueva version del backend, ejecutar `src/main/resources/sql/2026-11-04_lecturas_promedio.sql` en PostgreSQL. Hibernate no actualiza el esquema automaticamente.

Al generar una lectura de una nueva emision se calcula `lecturas.promedio`, con dos decimales, a partir del consumo (`lecturaactual - lecturaanterior`) de los seis meses calendario anteriores al periodo YYMM. Por ejemplo, 2611 considera 2605 a 2610. Se incluyen emisiones cerradas (estado 1), consumos no negativos y facturas no anuladas ni eliminadas. Si hay duplicados del mismo mes se toma la ultima lectura valida por idlectura. Los ceros son consumos validos; meses ausentes no cuentan en el divisor. Sin historial valido se guarda cero.

El promedio queda fijo: reintentar la apertura no recalcula lecturas existentes y JPA no actualiza esta columna. No se modifica abonados.promedio. Las lecturas historicas quedan con NULL y la interfaz conserva para ellas el promedio del abonado como referencia. No se hace una reconstruccion retroactiva.

La tabla, las alertas por consumo superior al doble del promedio y los reportes de lecturas/emisiones usan primero lecturas.promedio. Un cero guardado no activa el reemplazo por el promedio del abonado.

Nuevo se habilita con la ultima emision cerrada (estado 1) y los permisos de edicion existentes. El backend rechaza crear una emision si la ultima no esta cerrada o si existe alguna emision abierta.
