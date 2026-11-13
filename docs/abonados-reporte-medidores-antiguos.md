# Reporte de medidores por fecha de corte

`GET /abonados/reportes/medidores-antiguos?fechaCorte=2026-11-13`

Devuelve `fechaCorte`, `descripcion`, `cantidad` y `detalle`. La fecha es obligatoria en formato `YYYY-MM-DD`; un formato incorrecto devuelve HTTP 400.

Cuenta una vez cada abonado cuya instalación sea anterior a la fecha de corte menos siete años y cuyo acumulado supere 5.000 m³. Exactamente siete años o 5.000 m³ no califican. Incluye todo el día de corte.

El acumulado es la última lectura actual registrada entre la instalación y el corte menos la lectura inicial del abonado. No suma lecturas absolutas mensuales. Desempata por emisión y por identificador de lectura. Excluye cuentas sin fecha de instalación, lectura inicial o lectura fechada válida. No filtra por el estado actual del abonado, ya que ese estado no acredita su situación histórica.

El detalle incluye cuenta, medidor, cliente, ruta, categoría, estado, instalación, lectura inicial, fecha de última lectura, lectura actual y acumulado en m³. Ruta, categoría y estado corresponden a los datos actuales del abonado. Sin coincidencias, devuelve cantidad cero y detalle vacío.

En Abonados → Imprimir, seleccionar el reporte de medidores y la fecha de corte. Los botones Generar PDF y Generar Excel descargan el reporte completo. El Excel incluye indicador, cantidad y detalle con filtros, encabezados fijos y valores numéricos para las lecturas y el acumulado.

Limitación: utiliza los datos actuales de instalación y medidor de abonados. Los cambios de medidor, reinicios del contador o lecturas históricas faltantes requieren revisión; no reconstruye medidores reemplazados. El frontend debe incorporar esta consulta en los reportes de Abonados y presentar el selector de fecha de corte.
