package com.epmapat.erp_epmapat.repositorio;

/** Rubros cobrados: importes redondeados por linea e interes efectivamente cobrado una sola vez. */
public final class RubrosCobroSql {
    private RubrosCobroSql() {}
    private static final String RUBROS = """
, rubros_cobro AS (
    SELECT rf.idfactura_facturas, rf.idrubro_rubros, CAST(1 AS numeric) AS cantidad,
           CASE WHEN f.swmulta IS TRUE AND rf.idrubro_rubros IN (6, 1011) THEN 0
                WHEN f.swcondonar IS TRUE AND rf.idrubro_rubros = 6 THEN 0
                WHEN CAST(rf.cantidad * rf.valorunitario AS numeric) < 0
                  THEN FLOOR(CAST(rf.cantidad * rf.valorunitario AS numeric) * 100) / 100
                ELSE CEIL(CAST(rf.cantidad * rf.valorunitario AS numeric) * 100) / 100 END AS valorunitario,
           1 AS estado
    FROM rubroxfac rf JOIN facturas_cobro f ON f.idfactura = rf.idfactura_facturas
    WHERE f.fechacobro IS NOT NULL AND rf.idrubro_rubros NOT IN (5, 165)
      AND (rf.estado IS NULL OR rf.estado <> 0)
    UNION ALL
    SELECT f.idfactura, 5, CAST(1 AS numeric), CAST(COALESCE(f.interescobrado, 0) AS numeric), 1
    FROM facturas_cobro f WHERE f.fechacobro IS NOT NULL
      AND COALESCE(f.interescobrado, 0) <> 0
)
""";
    public static final String DIARIO = "WITH facturas_cobro AS (SELECT * FROM facturas WHERE fechacobro = ?1)" + RUBROS;
    public static final String RANGO = "WITH facturas_cobro AS (SELECT * FROM facturas WHERE fechacobro BETWEEN ?1 AND ?2)" + RUBROS;
}
